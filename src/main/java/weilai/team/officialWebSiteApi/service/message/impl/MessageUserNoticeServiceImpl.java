package weilai.team.officialWebSiteApi.service.message.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.transaction.annotation.Transactional;
import weilai.team.officialWebSiteApi.entity.admin.DO.User;
import weilai.team.officialWebSiteApi.entity.message.DTO.UpdateNoticeDTO;
import weilai.team.officialWebSiteApi.entity.message.Pagination;
import weilai.team.officialWebSiteApi.entity.message.VO.MessageNoticeVO;
import weilai.team.officialWebSiteApi.entity.post.DO.Post;
import weilai.team.officialWebSiteApi.entity.post.DTO.PostNoticeDto;
import weilai.team.officialWebSiteApi.mapper.admin.UserMapper;
import weilai.team.officialWebSiteApi.mapper.message.MessageUserNoticeMapper;
import weilai.team.officialWebSiteApi.mapper.post.PostMapper;
import weilai.team.officialWebSiteApi.service.message.MessageUserNoticeService;
import org.springframework.stereotype.Service;
import weilai.team.officialWebSiteApi.service.post.PostService;
import weilai.team.officialWebSiteApi.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static weilai.team.officialWebSiteApi.util.Values.*;

/**
* @author someo
* @description 针对表【message_user(公告-用户表)】的数据库操作Service实现
* @createDate 2024-11-18 17:40:43
*/
@Slf4j
@Service
public class MessageUserNoticeServiceImpl implements MessageUserNoticeService {

    @Resource
    RedisUtil redisUtil;

    @Resource
    UserUtil userUtil;

    @Resource
    PostService postService;


    @Resource
    PostMapper postMapper;

    @Resource
    MessageUserNoticeMapper messageUserNoticeMapper;

    @Resource  // 注入 StringRedisTemplate 类
    private StringRedisTemplate redis;

    @Resource
    UserMapper userMapper;

    @Resource
    private RedisCacheUtil redisCacheUtil;

    @Override
    public ResponseResult<?> getNotice(int page, int size, HttpServletRequest request) {
        String stringName=REDIS_NOTICE_COUNT;
//        int totalCount= redisUtil.getMessageCount(stringName,11,null);
        int totalCount= messageUserNoticeMapper.getAllNoticeCount();
        List<MessageNoticeVO> messageNoticeVOS=getNoticeFromRedis(page,size,totalCount,request);
        int n=messageNoticeVOS.size();
        if(n<size && (page-1)*size+n<totalCount){
            Page<MessageNoticeVO> pageInfo=new Page<>(page,size);
            messageNoticeVOS=messageUserNoticeMapper.selectNoticeByPageInfo(pageInfo);
            n=messageNoticeVOS.size();
        }

        int totalPage = (totalCount + size - 1) / size;
        int totalSize= n;
        // 将分页信息封装到一个Pagination对象中
        Pagination pagination = new Pagination(page, totalSize, totalCount, totalPage);
        Map<String,Object> map =new HashMap<>();
        map.put("PageInfo",pagination);
        map.put("AllMessages",messageNoticeVOS);
        map.put("anth",userUtil.getUserInfo(request).getAuth());
        // 返回包含消息数据和分页信息的ResponseResult
        return ResponseResult.OK.put(map) ; // 返回消息列表
    }

    @Override
    @Transactional
    public ResponseResult<?> markUnReadNoticeAsRead(Long noticeId, HttpServletRequest request) {
        log.info(String.valueOf(noticeId));
           Post post = postMapper.selectPostById(noticeId);
        if (post==null){
            return ResponseResult.POST_ID_ISNULL;
        }
        Long userId=userUtil.getUserInfo(request).getId();
        //存储到mysql
        messageUserNoticeMapper.markUnReadAsRead(userId,noticeId);
        //缓存到redis
        String setName=REDIS_NOTICE_USER_PREFIX+userId;
        //判断setName是否存在
        if(redis.hasKey(setName)){
            redisUtil.setRedisSet(setName,noticeId);
        }else {
            SetOperations<String, String> setOps = redis.opsForSet();
            List<Long> noticeIds = messageUserNoticeMapper.getReadNoticeIdsByUserId(userId);
            for (Long id : noticeIds) {
                setOps.add(setName, String.valueOf(id));

            }
        }

        return ResponseResult.OK;
    }

    @Override
    @Transactional
    public ResponseResult<?> markAllUnReadNoticeAsRead(HttpServletRequest request) {
        Long userId=userUtil.getUserInfo(request).getId();
        //存储到mysql
        messageUserNoticeMapper.markAllUnReadAsRead(userId);
        //缓存到redis
        SetOperations<String, String> setOps=redis.opsForSet();
        String setName=REDIS_NOTICE_USER_PREFIX+userId;
        List<Long> noticeIds=messageUserNoticeMapper.getAllNoticeIds();
        for (Long noticeId : noticeIds) {
            setOps.add(setName, String.valueOf(noticeId));
        }
        return ResponseResult.OK;
    }

    @Override
    @Transactional
    public ResponseResult<?> deleteNotice(Long noticeId, HttpServletRequest request) {
        Post post = postMapper.selectPostById(noticeId);
        if (post==null){
            return ResponseResult.POST_ID_ISNULL;
        }
        //删除缓存
        String ZSetName=REDIS_ALL_NOTICE;
        String stringName=REDIS_NOTICE_COUNT;
        redisUtil.decreaseMessageCount(stringName,1);
        redisUtil.deleteOneMessage(ZSetName, noticeId);
//        redisUtil
        //软删除mysql中记录
        postService.deletePostById(noticeId,request);
        messageUserNoticeMapper.deleteNotice(noticeId);
        return ResponseResult.OK;
    }

    @Override
    public ResponseResult<?> batchDeleteNotices(List<Long> noticeIds, HttpServletRequest request) {
        for (Long noticeId : noticeIds) {
            Post post = postMapper.selectPostById(noticeId);
            if (post!=null){
                //删除缓存
                String ZSetName=REDIS_ALL_NOTICE;
                redisUtil.deleteOneMessage(ZSetName, noticeId);
                //软删除mysql中记录
                postService.deletePostById(noticeId,request);
                messageUserNoticeMapper.deleteNotice(noticeId);
            }
        }
        String stringName=REDIS_NOTICE_COUNT;
        redisUtil.decreaseMessageCount(stringName,noticeIds.size());
        return ResponseResult.OK;
    }


    @Override
    public ResponseResult<?> getNotReadNoticeCount(HttpServletRequest request) {
        Long userId=userUtil.getUserInfo(request).getId();
        int count=messageUserNoticeMapper.getUnreadNoticeCount(userId);
        return ResponseResult.OK.put(count);
    }

//    @Override
//    public ResponseResult<?> getAllNoticeCount(HttpServletRequest request) {
//        int counts=redisUtil.getMessageCount(REDIS_NOTICE_COUNT,11,null);
//        return ResponseResult.OK.put(counts);
//    }

    @Override
    public ResponseResult<?> getAllNoticeCount(HttpServletRequest request) {
        int counts= messageUserNoticeMapper.getAllNoticeCount();
        return ResponseResult.OK.put(counts);
    }


    @Override
    public ResponseResult<?> getNoticeById(Long noticeId, HttpServletRequest request) {
        //先从redis中查询
        MessageNoticeVO messageNoticeVO;
        Map<String,Object> map =new HashMap<>();
        messageNoticeVO=getNoticeFromRedisById(noticeId,request);
        if(messageNoticeVO!=null){
            map.put("Message",messageNoticeVO);
            map.put("anth",userUtil.getUserInfo(request).getAuth());
            return ResponseResult.OK.put(map);
        }
        //从mysql中查询
        messageNoticeVO=messageUserNoticeMapper.selectNoticeByNoticeId(noticeId);
        if(messageNoticeVO!=null){
            map.put("Message",messageNoticeVO);
            map.put("anth",userUtil.getUserInfo(request).getAuth());
            return ResponseResult.OK.put(map);
        }
        return ResponseResult.POST_ID_ISNULL;

    }

    @Override
    @Transactional
    public ResponseResult<?> updateNotice(UpdateNoticeDTO updateNoticeDTO, HttpServletRequest request){
        User user=userUtil.getUserInfo(request);
        Long noticeId=updateNoticeDTO.getId();

        Post post = postMapper.selectPostById(noticeId);
        if (post==null){
            return ResponseResult.POST_ID_ISNULL;
        }
        //管理员的权限认证
        if(post.getUserId()!=userUtil.getUserInfo(request).getId()){
            return ResponseResult.NO_PERMISSION;
        }

        //删除公告
        postMapper.deleteById(post);
//        Date time=post.getPostTime();
        String postTxt=updateNoticeDTO.getPostTxt();
        String title= updateNoticeDTO.getTitle();
        Long userId= user.getId();
        Date newTime=new Date();
        post.setPostTxt(postTxt);
        post.setTitle(title);
        post.setUserId(userId);
        post.setPostTime(newTime);
        //在数据库中修改
//        postMapper.updateById(post);
        post.setId(null);
        postMapper.insert(post);
        Long newNoticeId=post.getId();
//        messageUserNoticeMapper.updateStatus(post.getId());
        //更改redis中的缓存
        PostNoticeDto postNoticeDto=new PostNoticeDto(newNoticeId,title,postTxt,userId,post.getPostTime());
        redisUtil.deleteOneMessage(REDIS_ALL_NOTICE, noticeId);
        redisUtil.setRedisZSetWithOutTime(REDIS_ALL_NOTICE, newNoticeId, postNoticeDto);
        redisUtil.deleteUserNoticeSet(REDIS_NOTICE_USER_PREFIX+newNoticeId);
        return ResponseResult.OK;
    }

    /**
     * 从redis中获取数据
     * @param page
     * @param size
     * @param request
     * @return
     */
    private List<MessageNoticeVO> getNoticeFromRedis( int page, int size, int totalCount,HttpServletRequest request) {

        String ZSetName=REDIS_ALL_NOTICE;

        // 获取 Redis 的 Sorted Set 操作接口
        ZSetOperations<String, String> zSetOps = redis.opsForZSet();

        // 计算 Redis 中分页的起始和结束位置
        int start = (page - 1) * size;  // 计算开始位置
        int end = start + size - 1;     // 计算结束位置

        //查询redis中的总条数
        long count = zSetOps.zCard(ZSetName);
        //如果redis中数据存放数据太少，将从mysql获取最新的30条数据并存储到redis中
        if(count<3 && count<totalCount){
            redisUtil.deleteAllMessageFromRedis(ZSetName);
            Page<PostNoticeDto> pageInfo=new Page<>(1,20);
            List<PostNoticeDto> postNoticeDtos=messageUserNoticeMapper.selectPostNoticeDtoByPageInfo(pageInfo);
            for (PostNoticeDto postNoticeDto : postNoticeDtos) {
                redisUtil.setRedisZSetWithOutTime(ZSetName, postNoticeDto.getNoticeId(), postNoticeDto);
            }

        }

        // 获取指定 setName 的所有数据，按时间戳排序，获取最新的 条数据
        Set<String> dataSet = zSetOps.reverseRange(ZSetName, start, end);  // 获取最新的数据
        List<MessageNoticeVO> messageNoticeVOS = new ArrayList<>();
        Long userId=userUtil.getUserInfo(request).getId();
        String setName=REDIS_NOTICE_USER_PREFIX+userId;
        if (dataSet != null && dataSet.size() != 0) {
            MessageNoticeVO messageNoticeVO;
            // 遍历 Redis 中的所有数据
            for (String jsonString : dataSet) {
                // 将 JSON 字符串反序列化为 MessageDTO 对象
                PostNoticeDto postNoticeDto = JSON.parseObject(jsonString, PostNoticeDto.class);
                //获取用户信息
                User sender=getUserInfo(postNoticeDto.getUserId());
                boolean f=checkIsRead(postNoticeDto.getNoticeId(), userId,setName);
                //将信息和发送信息的用户信息以及status封装到一起
                if(sender==null){
                    if(f){
                        messageNoticeVO=new MessageNoticeVO(postNoticeDto,null,null,1);
                    }else {
                        messageNoticeVO=new MessageNoticeVO(postNoticeDto,null,null,0);
                    }
                }else{
                    if(f){
                        messageNoticeVO=new MessageNoticeVO(postNoticeDto, sender.getName(), sender.getHeadPortrait(), 1);
                    }else {
                        messageNoticeVO=new MessageNoticeVO(postNoticeDto,sender.getName(), sender.getHeadPortrait(),0);
                    }
                }
                messageNoticeVOS.add(messageNoticeVO);
            }
        }
        return messageNoticeVOS;
    }

   private User getUserInfo(Long userId){
       return redisCacheUtil.queryWithPassThroughByString(
               Values.USER_INFO_PREFIX,
               userId,
               User.class,()-> userMapper.selectById(userId),
               Values.USER_INFO_OUT_TIME,
               TimeUnit.HOURS);
   }


    /**
     * 查看用户对公告是否已读
     * @param noticeId
     * @param userId
     * @return
     */
    private boolean checkIsRead(Long noticeId, Long userId,String setName){

        SetOperations<String, String> setOps=redis.opsForSet();

        if(!redis.hasKey(setName)){
            //将关于该贴的用户是否已读信息存储起来
            List<Long> noticeIds=messageUserNoticeMapper.getReadNoticeIdsByUserId(userId);
            for (Long id : noticeIds) {
                //将关于此用户已读的公告存储起来
                setOps.add(setName, String.valueOf(id));
            }
        }
        return setOps.isMember(setName,String.valueOf(noticeId));

    }

    private MessageNoticeVO getNoticeFromRedisById(Long noticeId,HttpServletRequest request){
        Long score=noticeId;
        String ZSetName=REDIS_ALL_NOTICE;
        ZSetOperations<String, String> zSetOps = redis.opsForZSet();
        // 根据指定的分数范围获取成员，这里范围设置为从 score 到 score
        Set<String> dataSet = zSetOps.rangeByScore(ZSetName, score, score);
        Long userId=userUtil.getUserInfo(request).getId();
        String setName=REDIS_NOTICE_USER_PREFIX+userId;
        if (dataSet != null && dataSet.size() != 0) {
            MessageNoticeVO messageNoticeVO;
            // 遍历 Redis 中的所有数据
            for (String jsonString : dataSet) {
                // 将 JSON 字符串反序列化为 MessageDTO 对象
                PostNoticeDto postNoticeDto = JSON.parseObject(jsonString, PostNoticeDto.class);
                //获取用户信息
                User sender=getUserInfo(postNoticeDto.getUserId());
                boolean f=checkIsRead(postNoticeDto.getNoticeId(), userId,setName);
                //将信息和发送信息的用户信息以及status封装到一起
                if(sender==null){
                    if(f){
                        messageNoticeVO=new MessageNoticeVO(postNoticeDto,null,null,1);
                    }else {
                        messageNoticeVO=new MessageNoticeVO(postNoticeDto,null,null,0);
                    }
                }else{
                    if(f){
                        messageNoticeVO=new MessageNoticeVO(postNoticeDto, sender.getName(), sender.getHeadPortrait(), 1);
                    }else {
                        messageNoticeVO=new MessageNoticeVO(postNoticeDto,sender.getName(), sender.getHeadPortrait(),0);
                    }
                }
                return messageNoticeVO;
            }
        }
        return null;
    }

}




