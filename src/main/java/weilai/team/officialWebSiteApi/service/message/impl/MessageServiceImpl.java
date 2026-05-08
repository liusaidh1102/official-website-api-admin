package weilai.team.officialWebSiteApi.service.message.impl;


import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import weilai.team.officialWebSiteApi.entity.admin.DO.User;
import weilai.team.officialWebSiteApi.entity.message.DO.Message;
import weilai.team.officialWebSiteApi.entity.message.DTO.MessagePostDTO;
import weilai.team.officialWebSiteApi.entity.message.Pagination;
import weilai.team.officialWebSiteApi.entity.message.VO.MessageVO;
import weilai.team.officialWebSiteApi.mapper.admin.UserMapper;
import weilai.team.officialWebSiteApi.mapper.message.MessageMapper;
import weilai.team.officialWebSiteApi.service.message.MessageService;
import org.springframework.stereotype.Service;
import weilai.team.officialWebSiteApi.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static weilai.team.officialWebSiteApi.util.Values.*;

/**
* @author someo
* @description 针对表【message(消息表)】的数据库操作Service实现
* @createDate 2024-11-10 10:28:21
*/
@Service
public class MessageServiceImpl implements MessageService {

    @Resource
    private MessageMapper messageMapper;

    @Resource
    private UserUtil userUtil;

    @Resource
    private RedisUtil redisUtil;

    @Resource  // 注入 StringRedisTemplate 类
    private StringRedisTemplate redis;

    @Resource
    RedisCacheUtil redisCacheUtil;

    @Resource
    UserMapper userMapper;


    @Override
    public ResponseResult<?> getMessageInfoByType(int messageType, int page, int size, HttpServletRequest request) {
        Long receiverId=userUtil.getUserInfo(request).getId();
        String stringName="";
        String ZSetName="";
        switch (messageType){
            case 1:
                ZSetName =REDIS_LIKE_OR_COLLECT_PREFIX+receiverId;
                stringName=REDIS_ALL_LIKE_OR_COLLECT_COUNT_PREFIX+receiverId;
                break;
            case 3:
                ZSetName =REDIS_COMMENT_PREFIX+receiverId;
                stringName=REDIS_ALL_COMMENT_COUNT_PREFIX+receiverId;
                break;
            case 5:
                ZSetName=REDIS_SYSTEM_NOTIFICATION_PREFIX+receiverId;
                stringName=REDIS_ALL_SYSTEM_NOTIFICATION_COUNT_PREFIX+receiverId;
                break;
        }
        int totalCount=redisUtil.getMessageCount(stringName,messageType,receiverId);
//        int totalCount= messageMapper.getMessageCountByMessageType(receiverId,messageType);
        List<MessageVO> messageVOS =getMessageFromRedis(ZSetName,page,size,messageType,totalCount,request);
        //redis存储数据不够时
        int n=messageVOS.size();
        if(n<size && (page-1)*size+n<totalCount){
            Page<MessageVO> pageInfo=new Page<>(page,size);
            messageVOS=messageMapper.selectMessageByReceiverId(pageInfo,receiverId,messageType);
            n=messageVOS.size();
        }
        int totalPage = (totalCount + size - 1) / size;
        int totalSize= n;
        // 将分页信息封装到一个Pagination对象中
        Pagination pagination = new Pagination(page, totalSize, totalCount, totalPage);
        Map<String,Object> map =new HashMap<>();
        map.put("PageInfo",pagination);
        map.put("AllMessages",messageVOS);

        // 返回包含消息数据和分页信息的ResponseResult
        return ResponseResult.OK.put(map) ; // 返回消息列表
    }

    @Override
    public ResponseResult<?> getNotReadCountByMessageType(int messageType,HttpServletRequest request) {
        Long receiverId=userUtil.getUserInfo(request).getId();
        //从缓存中获取
        String stringName="";
        switch (messageType){
            case 1:
                stringName=REDIS_NOT_READ_LIKE_OR_COLLECT_COUNT_PREFIX+receiverId;
                break;
            case 3:
            case 4:
                stringName=REDIS_NOT_READ_COMMENT_COUNT_PREFIX+receiverId;
                break;
            case 5:
                stringName=REDIS_NOT_READ_SYSTEM_NOTIFICATION_COUNT_PREFIX+receiverId;
                break;
        }
        int count= redisUtil.getMessageCount(stringName,messageType*2,receiverId);
        return ResponseResult.OK.put(count);
    }

    @Override
    public ResponseResult<?> markUnreadAsReadByMessageType(int messageType,HttpServletRequest request) {
        Long receiverId=userUtil.getUserInfo(request).getId();
        //先将redis中的未读消息数量改为0
        String stringName="";
        switch (messageType){
            case 1:
            case 2:
                stringName=REDIS_NOT_READ_LIKE_OR_COLLECT_COUNT_PREFIX+receiverId;
                break;
            case 3:
            case 4:
                stringName=REDIS_NOT_READ_COMMENT_COUNT_PREFIX+receiverId;
                break;
            case 5:
                stringName=REDIS_NOT_READ_SYSTEM_NOTIFICATION_COUNT_PREFIX+receiverId;
                break;
        }
        //redis
        redisUtil.markMessageAsRead(stringName);
        //mysql
        messageMapper.markUnreadMessagesAsReadByMessageType(receiverId,messageType);
        return ResponseResult.OK;
    }



    @Override
    public ResponseResult<?> deleteAllMessages(int messageType,HttpServletRequest request) {
        Long receiverId=userUtil.getUserInfo(request).getId();
        String ZSetName ="";
        String stringName1="";
        String stringName2="";
        switch (messageType){
            case 1:
            case 2:
                ZSetName =REDIS_LIKE_OR_COLLECT_PREFIX+receiverId;
                stringName1=REDIS_NOT_READ_LIKE_OR_COLLECT_COUNT_PREFIX+receiverId;
                stringName2=REDIS_ALL_LIKE_OR_COLLECT_COUNT_PREFIX+receiverId;

                break;
            case 3:
            case 4:
                //插入redis
                ZSetName =REDIS_COMMENT_PREFIX+receiverId;
                stringName1=REDIS_NOT_READ_COMMENT_COUNT_PREFIX+receiverId;
                stringName2=REDIS_ALL_COMMENT_COUNT_PREFIX+receiverId;
                break;
            case 5:
                ZSetName=REDIS_SYSTEM_NOTIFICATION_PREFIX+receiverId;
                stringName1=REDIS_NOT_READ_SYSTEM_NOTIFICATION_COUNT_PREFIX+receiverId;
                stringName2=REDIS_ALL_SYSTEM_NOTIFICATION_COUNT_PREFIX+receiverId;
                break;
        }
        //删除redis缓存中的数据
        redisUtil.deleteAllMessageFromRedis(ZSetName);
        redisUtil.deleteRedis(stringName1);
        redisUtil.deleteRedis(stringName2);
        messageMapper.deleteAllMessagesByMessageType(receiverId,messageType);
        return ResponseResult.OK;
    }

    @Override
    public ResponseResult<?> deleteMessageByMessageId(Long messageId,HttpServletRequest request) {
        Long receiverId=userUtil.getUserInfo(request).getId();
        //先判断是否存在
        Message message =messageMapper.selectMessageByMessageId(messageId);

        if(message !=null){
            //判断是否有权删除
            if(receiverId == message.getReceiverId())
            {
                String ZSetName="";
                String stringName1 = "";
                String stringName2="";
                int messageType=message.getMessageType();
                switch (messageType){
                    case 1:
                    case 2:
                        ZSetName =REDIS_LIKE_OR_COLLECT_PREFIX+receiverId;
                        stringName1=REDIS_NOT_READ_LIKE_OR_COLLECT_COUNT_PREFIX+receiverId;
                        stringName2=REDIS_ALL_LIKE_OR_COLLECT_COUNT_PREFIX+receiverId;

                        break;
                    case 3:
                    case 4:
                        //插入redis
                        ZSetName =REDIS_COMMENT_PREFIX+receiverId;
                        stringName1=REDIS_NOT_READ_COMMENT_COUNT_PREFIX+receiverId;
                        stringName2=REDIS_ALL_COMMENT_COUNT_PREFIX+receiverId;
                        break;
                    case 5:
                        ZSetName=REDIS_SYSTEM_NOTIFICATION_PREFIX+receiverId;
                        stringName1=REDIS_NOT_READ_SYSTEM_NOTIFICATION_COUNT_PREFIX+receiverId;
                        stringName2=REDIS_ALL_SYSTEM_NOTIFICATION_COUNT_PREFIX+receiverId;
                        break;
                }
                redisUtil.deleteRedis(ZSetName);
                redisUtil.deleteRedis(stringName1);
                redisUtil.deleteRedis(stringName2);
                
                //再删除数据库中的信息
                messageMapper.deleteMessageByMessageId(messageId);
                return ResponseResult.OK;
            }
            return ResponseResult.NO_PERMISSION;
        }
        return ResponseResult.NOT_FOUND_MESSAGE;
    }


    /**
     * 分页获取redis中的数据
     * @param ZSetName Redis中Sorted Set的名称
     * @param page 页数
     * @param size 每页的数量
     * @return 泛型列表
     */
    private  List<MessageVO> getMessageFromRedis(String ZSetName, int page, int size,int messageType,int totalCount,HttpServletRequest request){
        // 获取 Redis 的 Sorted Set 操作接口
        ZSetOperations<String, String> zSetOps = redis.opsForZSet();
        Long n=zSetOps.zCard(ZSetName);

        //如果redis中的数据太少的话，从mysql中获取，放到redis中
        if(totalCount>n && n<10){
            //将原来的存储的信息删除，避免重复
            redisUtil.deleteAllMessageFromRedis(ZSetName);
            Page<MessagePostDTO> pageInfo=new Page<>(1,30);
            List<MessagePostDTO> messagePostDTOS=messageMapper.getMessagePostDTO(pageInfo,userUtil.getUserInfo(request).getId(),messageType);
            for (MessagePostDTO messagePostDTO : messagePostDTOS) {
                redisUtil.setRedisZSetWithOutTime(ZSetName,messagePostDTO.getMessageId(),messagePostDTO);
            }
        }
        n=zSetOps.zCard(ZSetName);
        // 计算 Redis 中分页的起始和结束位置
        long start = (page - 1) * size;  // 计算开始位置
        long end = Math.min(start + size - 1, n - 1); // 确保结束位置不超过集合大小

        // 获取指定 setName 的数据，按时间戳排序（假设是降序），获取指定范围的数据
        Set<String> dataSet = zSetOps.reverseRange(ZSetName, start, end);

        List<MessageVO> resultList = new ArrayList<>();
        if (dataSet != null) {
            // 遍历 Redis 中的所有数据
            MessageVO messageVO;
            for (String jsonString : dataSet) {
                // 将 JSON 字符串反序列化为指定类型的对象
                MessagePostDTO messagePostDTO = JSON.parseObject(jsonString, MessagePostDTO.class);
                User user=getUserInfo(messagePostDTO.getSenderId());
                if(user!=null){
                    messageVO=new MessageVO(messagePostDTO,user.getName(),user.getHeadPortrait());
                }else {
                    messageVO=new MessageVO(messagePostDTO,null,null);
                }
                resultList.add(messageVO);
            }
        }

        return resultList;
    }


    private User getUserInfo(Long userId){
        return redisCacheUtil.queryWithPassThroughByString(
                Values.USER_INFO_PREFIX,
                userId,
                User.class,()-> userMapper.selectById(userId),
                Values.USER_INFO_OUT_TIME,
                TimeUnit.HOURS);
    }


}




