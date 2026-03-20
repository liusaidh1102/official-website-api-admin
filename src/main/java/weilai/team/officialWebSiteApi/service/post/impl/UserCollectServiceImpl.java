package weilai.team.officialWebSiteApi.service.post.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import weilai.team.officialWebSiteApi.entity.admin.DO.User;
import weilai.team.officialWebSiteApi.entity.message.DO.Message;
import weilai.team.officialWebSiteApi.entity.post.DO.Post;
import weilai.team.officialWebSiteApi.entity.post.DO.UserCollect;
import weilai.team.officialWebSiteApi.mapper.post.PostMapper;
import weilai.team.officialWebSiteApi.service.message.sendMessage.MessageProducer;
import weilai.team.officialWebSiteApi.service.post.UserCollectService;
import weilai.team.officialWebSiteApi.mapper.post.UserCollectMapper;
import org.springframework.stereotype.Service;

import weilai.team.officialWebSiteApi.util.RedisUtil;
import weilai.team.officialWebSiteApi.util.ResponseResult;
import weilai.team.officialWebSiteApi.util.UserUtil;
import weilai.team.officialWebSiteApi.util.Values;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;


/**
* @author 杜昱徵
* @description 针对表【user_collect(用户的收藏[贴子])】的数据库操作Service实现
* @createDate 2024-11-13 16:30:33
*/
@Service
@Transactional
public class UserCollectServiceImpl extends ServiceImpl<UserCollectMapper, UserCollect>
    implements UserCollectService{
    @Resource
    private PostMapper postMapper;
    @Resource
    private UserCollectMapper userCollectMapper;
    @Resource
    private UserUtil userUtil;
    @Resource
    private MessageProducer messageProducer;
    @Resource
    private RedisUtil redisUtil;

    /**
     * 收藏帖子
     * @param id
     * @return
     */
    @Override
    public ResponseResult<?> collect(Long id, HttpServletRequest request) {
        // 获取用户信息
        User userInfo = userUtil.getUserInfo(request);

        if (userInfo == null) {
            return ResponseResult.Unauthorized;
        }

        // 更新用户个人中心的收藏
        redisUtil.deleteRedis(Values.USER_COLLECT_PREFIX + userInfo.getId());

        // 判断帖子是否存在
        Post post = postMapper.selectPostById(id);
        if (post == null) {
            return ResponseResult.POST_ID_ISNULL;
        }

        // 获取 Redis 中的收藏集合 Key
        String collectKey = Values.IS_COLLECT__KEY + id;
        // 使用 Lua 脚本实现原子性操作
        String luaScript = "local isCollected = redis.call('SISMEMBER', KEYS[1], ARGV[1])\n" +
                "if isCollected == 0 then\n" +
                "    redis.call('SADD', KEYS[1], ARGV[1])\n" +
                "    return 1\n" +
                "else\n" +
                "    redis.call('SREM', KEYS[1], ARGV[1])\n" +
                "    return 0\n" +
                "end";

        // 执行 Lua 脚本
        Long userId = userInfo.getId();
        List<String> keys = Collections.singletonList(collectKey);
        Object result = redisUtil.executeLua(luaScript, keys, Collections.singletonList(userId.toString()));

        try {
            if ((Long) result == 1) {
                // 收藏成功
                int row = userCollectMapper.addCollect(userId, id);
                if(row <= 0){
                    UserCollect userCollect=new UserCollect();
                    userCollect.setUserId(userId);
                    userCollect.setCollectPost(id);
                    userCollect.setCollectTime(new Date());
                    row=userCollectMapper.insert(userCollect);
                }
                if (row > 0) {
                  UpdateRedisAndNotify(userId, post.getUserId(), id, true,request); // 异步更新 Redis 和发送通知
                    return ResponseResult.COLLECTION_SUCCESS;
                }
                return ResponseResult.COLLECTION_FAIL;
            } else {
                // 取消收藏成功
                boolean success = userCollectMapper.deleteByPostId(userId, id);
                if (success) {
                    UpdateRedisAndNotify(userId, post.getUserId(), id, false,request); // 异步更新 Redis 和发送通知

                    return ResponseResult.DELETE_COLLECTION_SUCCESS;
                }
                return ResponseResult.DELETE_COLLECTION_FAIL;
            }
        } catch (Exception e) {
            log.error("收藏操作失败", e);
            return ResponseResult.SERVICE_ERROR;
        }
    }
    /**
     * 异步更新 Redis 和发送通知
     */
    private void UpdateRedisAndNotify(Long userId, Long authorId, Long postId, boolean isCollect, HttpServletRequest request) {
            try {
                // 更新 Redis
                String collectKey = Values.IS_COLLECT__KEY + postId;
                if (isCollect) {
                    redisUtil.addCollect(collectKey, userId.toString());
                } else {
                    redisUtil.removeCollect(collectKey, userId.toString());
                }
                // 发送通知
                Message message = new Message(userId, authorId, postId, 2, null);
                messageProducer.sendToUser(message,request);
            } catch (Exception e) {
                log.error("异步更新 Redis 或发送通知失败", e);
            }
    }
}




