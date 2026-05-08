package weilai.team.officialWebSiteApi.service.message.sendMessage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import weilai.team.officialWebSiteApi.entity.message.DTO.MessageNoticeDTO;
import weilai.team.officialWebSiteApi.entity.message.VO.MessageNoticeVO;
import weilai.team.officialWebSiteApi.entity.message.VO.MessageVO;
import weilai.team.officialWebSiteApi.util.ResponseResult;
import weilai.team.officialWebSiteApi.util.UserUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Slf4j
@Service
public class SseService {

    @Resource
    UserUtil userUtil;

    private static final Map<Long, SseEmitter> sseCache = new ConcurrentHashMap<>();


    /**
     * 添加连接的客户端
     * @return
     */
    public SseEmitter addClient(HttpServletRequest request) {
        log.info("--------------------");
        Long userId=userUtil.getUserInfo(request).getId();
        if(sseCache.containsKey(userId)){
            SseEmitter sseEmitter = sseCache.get(userId);
            sseCache.remove(userId);
            sseEmitter.complete();
        }
        SseEmitter sseEmitter = new SseEmitter(1000 * 60 * 30L); // 30分钟超时

        // 注册回调
        sseEmitter.onCompletion(completionCallBack(userId));     // 长链接完成后回调接口(即关闭连接时调用)
        sseEmitter.onTimeout(timeoutCallBack(userId));        // 连接超时回调
//        sseEmitter.onError(errorCallBack(userId));          // 推送消息异常时，回调方法

        // 存储用户ID和Emitter的映射
        sseCache.put(userId, sseEmitter);

        try {
            sseEmitter.send(SseEmitter.event().data(ResponseResult.SSE_CONNECTION_SUCCESS, MediaType.APPLICATION_JSON));
        }catch (Exception e){
            System.out.println("创建ss连接异常,客户端id:"+userId);
            e.printStackTrace();
        }
        log.info("客户端连接成功，用户ID: " + userId);
        log.info(String.valueOf(sseCache.containsKey(userId)));
        return sseEmitter;
    }

    /**
     * 向指定用户发送消息
     * @param messageVO 信息
     * @param receiverId 接收者的ID
     */
    public void sendToUser(MessageVO messageVO, Long receiverId) {
        SseEmitter emitter = sseCache.get(receiverId);
        if (emitter != null) {
            try {
                log.info("sse发送了一条用户信息");
                emitter.send(SseEmitter.event()
                        .name("message")
                        .data(messageVO));  // 向指定的用户发送消息
            } catch (IOException e) {
                sseCache.remove(receiverId);  // 如果发送失败，移除这个 emitter
                //关闭与客户端的连接
                emitter.complete();

            }
        }

    }

    /**
     *  向所有客户端发送消息
     * @param messageNoticeDTO 公告信息
     */
    public void sendToAll(MessageNoticeDTO messageNoticeDTO) {
        //循环遍历每个在线用户
        for (Map.Entry<Long, SseEmitter> entry : sseCache.entrySet()) {
            SseEmitter sseEmitter = entry.getValue();
            try {
                sseEmitter=entry.getValue();
                sseEmitter.send(SseEmitter.event()
                        .name("notice")
                        .data(messageNoticeDTO));
            } catch (IOException e) {
                sseCache.remove(entry.getKey());
                //关闭与客户端的连接
                sseEmitter.complete();
            }
        }
    }

    /**
     * 长链接完成后回调接口(即关闭连接时调用)
     * @param clientId
     * @return
     */
    private Runnable completionCallBack(Long clientId) {
        return () -> {
            System.out.println("结束连接:"+clientId);
            removeUser(clientId);
        };
    }

    /**
     * 连接超时回调
     * @param clientId
     * @return
     */
    private Runnable timeoutCallBack(Long clientId){
        return ()->{
            System.out.println("连接超时:"+clientId);
            removeUser(clientId);
        };
    }
    /**
     * 推送消息异常时，回调方法
     * @param clientId
     * @return
     */
    private Consumer<Throwable> errorCallBack(Long clientId){
        return throwable -> {
            System.out.println("连接异常:客户端ID:"+clientId);

            // 推送消息失败后 每隔1s 推送一次 推送5次
            for (int i = 0;i<5;i++){
                try {
                    Thread.sleep(1000);
                    SseEmitter sseEmitter = sseCache.get(clientId);
                    if (sseEmitter == null){
                        System.out.println("第"+i+"次消息重推失败,未获取到"+clientId+"对应的长链接");
                        continue;
                    }
                    sseEmitter.send("失败后重新推送");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
    }


    /**
     * 删除用户
     * @param clientId
     */
    private void  removeUser(Long clientId){
        sseCache.remove(clientId);
        System.out.println("移除用户:"+clientId);
    }


//    @Scheduled(fixedRate = 15000) // 每15秒执行一次
//    public void sendScheduledMessage() {
//        // 直接调用异步方法
//        sendMessageAsync();
//    }
//
//    @Async
//    public CompletableFuture<Void> sendMessageAsync() {
//        MessageVO messageVO = new MessageVO(1L, 3L, "肖晴", null, 2L, "考试", "不想和考试", new Date(), 1);
//        SseEmitter emitter = sseCache.get(5L);
//        if (emitter != null) {
//            try {
//                log.info("发送了一条信息");
//                emitter.send(SseEmitter.event()
//                        .name("message")
//                        .data(messageVO));
//            } catch (IOException e) {
//                sseCache.remove(5L); // 如果发送失败，从缓存中移除emitter
//                emitter.completeWithError(e); // 向客户端发送错误并完成连接
//            }
//        }
//        return CompletableFuture.completedFuture(null);
//    }
//
//    @Scheduled(fixedRate = 15000) // 每15秒执行一次
//    public void sendScheduledNotice() {
//        // 直接调用异步方法
//        sendNoticeAsync();
//    }
//
//    @Async
//    public CompletableFuture<Void> sendNoticeAsync() {
//        MessageNoticeDTO messageNoticeDTO=new MessageNoticeDTO(1L,"公告","公告公告公告！！！",new Date(),3L,"肖晴",null);
//        SseEmitter emitter = sseCache.get(5L);
//        if (emitter != null) {
//            try {
//                log.info("发送了一条信息");
//                emitter.send(SseEmitter.event()
//                        .name("notice")
//                        .data(messageNoticeDTO));
//            } catch (IOException e) {
//                sseCache.remove(5L); // 如果发送失败，从缓存中移除emitter
//                emitter.completeWithError(e); // 向客户端发送错误并完成连接
//            }
//        }
//        return CompletableFuture.completedFuture(null);
//    }

}
