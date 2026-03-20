package weilai.team.officialWebSiteApi.service.postComment.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import weilai.team.officialWebSiteApi.entity.admin.DO.User;
import weilai.team.officialWebSiteApi.entity.message.DO.Message;
import weilai.team.officialWebSiteApi.entity.post.VO.PostVo;
import weilai.team.officialWebSiteApi.entity.postComment.DO.PostCommentAll;
import weilai.team.officialWebSiteApi.entity.postComment.DO.PostCommentOne;
import weilai.team.officialWebSiteApi.entity.postComment.VO.CommentAllVO;
import weilai.team.officialWebSiteApi.entity.postComment.VO.CommentOneVO;
import weilai.team.officialWebSiteApi.entity.postComment.DTO.ReplyCommentDTO;
import weilai.team.officialWebSiteApi.entity.postComment.DTO.WritePostCommentDTO;
import weilai.team.officialWebSiteApi.mapper.admin.UserMapper;
import weilai.team.officialWebSiteApi.mapper.post.PostMapper;
import weilai.team.officialWebSiteApi.mapper.postComment.PostCommentAllMapper;
import weilai.team.officialWebSiteApi.mapper.postComment.PostCommentOneMapper;
import weilai.team.officialWebSiteApi.service.message.sendMessage.MessageProducer;
import weilai.team.officialWebSiteApi.service.postComment.PostCommentService;
import org.springframework.stereotype.Service;
import weilai.team.officialWebSiteApi.service.recruit.FileService;
import weilai.team.officialWebSiteApi.util.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
* @author 王科林
* @description 针对表【post_comment_photo(评论的图片)】的数据库操作Service实现
* @createDate 2024-11-15 15:10:38
*/
@Service
public class PostCommentServiceImpl implements PostCommentService {

    @Resource
    private UserUtil userUtil;

    @Resource
    private PostCommentOneMapper postCommentOneMapper;

    @Resource
    private PostCommentAllMapper postCommentAllMapper;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private MessageProducer messageProducer;

    @Resource
    private PostMapper postMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisCacheUtil redisCacheUtil;

    @Override
    @Transactional
    public ResponseResult<?> writePostComment(WritePostCommentDTO writePostCommentDTO, HttpServletRequest request) {
        Long userId = userUtil.getUserInfo(request).getId();

        //判断贴子是否存在
        PostVo post = postMapper.getById(writePostCommentDTO.getPostId());
        if(post == null){
            return ResponseResult.POST_ID_ISNULL;
        }

        //判断是否有帖子评论数redis如果有缓存则删除更新
        if(redisUtil.isExist(Values.COMMENT_COUNT_KEY+writePostCommentDTO.getPostId())){
            redisUtil.deleteRedis(Values.COMMENT_COUNT_KEY+writePostCommentDTO.getPostId());
        }
        //贴子的评论的内容不能全为空
        boolean a = writePostCommentDTO.getCommentTxt() == null || MyString.isNull(writePostCommentDTO.getCommentTxt());
        if(a) return ResponseResult.Bad_Request;

        // 1. 保存评论，并上传一级评论
        PostCommentOne comment = new PostCommentOne();
        comment.setCommentUser(userId); //设置用户
        comment.setCommentTime(new Date()); //设置时间
        comment.setPostId(writePostCommentDTO.getPostId()); //设置贴子id
        comment.setCommentTxt(writePostCommentDTO.getCommentTxt()); //设置评论内容
        int i1 = postCommentOneMapper.insert(comment);
        if(i1 <= 0) return ResponseResult.SERVICE_ERROR;

        //发送信息提醒贴子用户，由用户评论了
        Message message =
                new Message(userId,post.getUserId(),writePostCommentDTO.getPostId(),3,writePostCommentDTO.getCommentTxt());
        messageProducer.sendToUser(message, request);

        return ResponseResult.OK;
    }

    @Override
    @Transactional
    public ResponseResult<?> replyComment(ReplyCommentDTO replyCommentDTO, HttpServletRequest request) {
        Long userId = userUtil.getUserInfo(request).getId();

        //判断用户是否存在
        User user = userMapper.selectById(replyCommentDTO.getUserId());
        if(user == null) return ResponseResult.USER_NOT_FOUND;

        //判断是否有帖子评论数redis如果有缓存则删除更新
        Integer postId = postCommentOneMapper.selectPostId(replyCommentDTO.getCommentId());
        if(redisUtil.isExist(Values.COMMENT_COUNT_KEY+postId)){
            redisUtil.deleteRedis(Values.COMMENT_COUNT_KEY+postId);
        }
        //贴子的评论的内容不能全为空
        boolean a = replyCommentDTO.getCommentTxt() == null || MyString.isNull(replyCommentDTO.getCommentTxt());
        if(a) return ResponseResult.Bad_Request;

        //创建多级评论的对象
        PostCommentAll commentAll = new PostCommentAll();
        commentAll.setCommentUser(userId); // 评论者id
        commentAll.setCommentTime(new Date()); // 评论时间
        commentAll.setCommentId(replyCommentDTO.getCommentId()); //一级评论id
        commentAll.setPointUser(replyCommentDTO.getUserId()); // @用户的id
        commentAll.setCommentTxt(replyCommentDTO.getCommentTxt()); //评论内容
        int i1 = postCommentAllMapper.insert(commentAll);
        if(i1 <= 0) return ResponseResult.SERVICE_ERROR;

        //发送信息提醒贴子用户，有用户@你了评论了
        Message message =
                new Message(userId,replyCommentDTO.getUserId(),Long.valueOf(postId),4,replyCommentDTO.getCommentTxt());
        messageProducer.sendToUser(message, request);

        return ResponseResult.OK;
    }

    @Override
    public ResponseResult<?> deleteComment(Long commentId,boolean isNotFromPost,HttpServletRequest request) {
        //获取当前登录用户id
        Long userId = userUtil.getUserInfo(request).getId();
        int i;
         //判断是否有帖子评论数redis如果有缓存则删除更新
        Integer postId = postCommentOneMapper.selectPostId(commentId);
        if(redisUtil.isExist(Values.COMMENT_COUNT_KEY+postId)){
            redisUtil.deleteRedis(Values.COMMENT_COUNT_KEY+postId);
        }

        if(commentId > 0){ //大于0，表示一级评论
            //如果当前登录用户id和评论用户id不一致，说明不是自己的评论
            PostCommentOne postCommentOne = postCommentOneMapper.selectById(commentId);
            Long commentUser = postCommentOne.getCommentUser();
            if(!userId.equals(commentUser) && isNotFromPost) {
                return ResponseResult.FORBIDDEN;
            }
            postCommentOne.setDeleteFlag(1);
            i = postCommentOneMapper.updateById(postCommentOne);

            //查询一级评论下二评论
            List<Long> list = postCommentAllMapper.selectIdByCommentId(commentId);

            //运用stream流将list中的所有元素都转换成其相反数
            list = list.stream().map(Math::negateExact).collect(Collectors.toList());

            //删除一级评论下的所有二级评论的所有点赞数
            list.forEach((l)-> stringRedisTemplate.delete(Values.LIKE_COMMENTS_PREFIX + l));

            //删除一级评论下的所有二级评论
            postCommentAllMapper.updateDeleteFlagByCommentId(1,commentId);


        } else { //小于0，表示多级评论
            //如果当前登录用户id和评论用户id不一致，说明不是自己的评论
            PostCommentAll postCommentAll = postCommentAllMapper.selectById(-commentId);
            Long commentUser = postCommentAll.getCommentUser();
            if(!userId.equals(commentUser)) return ResponseResult.FORBIDDEN;
            postCommentAll.setDeleteFlag(1);
            i = postCommentAllMapper.updateById(postCommentAll);

        }

        //删除点赞数
        stringRedisTemplate.delete(Values.LIKE_COMMENTS_PREFIX + commentId);

        return i <= 0 ? ResponseResult.COMMENT_NOT_FOUND : ResponseResult.OK;
    }

    @Override
    public ResponseResult<?> likeOption(Long commentId, HttpServletRequest request) {
        //获取当前用户的id
        Long userId = userUtil.getUserInfo(request).getId();

        //判断评论是否存在
        boolean a = postCommentOneMapper.selectById(Math.abs(commentId)) == null;
        boolean b = postCommentAllMapper.selectById(Math.abs(commentId)) == null;
        if(a && b){ //点赞评论不存在
            return ResponseResult.COMMENT_NOT_FOUND;
        }

        //添加点赞数，如果点赞过，则取消点赞
        boolean isAddLike = addLike(Values.LIKE_COMMENTS_PREFIX + commentId, userId);
        Long size = stringRedisTemplate.opsForSet().size(Values.LIKE_COMMENTS_PREFIX + commentId);
        if(size == null) size = 0L;
        if(isAddLike){
            //点赞成功
            if(commentId > 0) postCommentOneMapper.updateLikeCountById(size,commentId);
            else postCommentAllMapper.updateLikeCountById(size,-commentId);
        } else {
            //取消点赞
            if(commentId > 0) postCommentOneMapper.updateLikeCountById(size,commentId);
            else postCommentAllMapper.updateLikeCountById(size,-commentId);
        }

        //返回数据
        return ResponseResult.OK;
    }

    @Override
    public ResponseResult<?> getCommentOne(Long postId, Integer pageNumber, Integer pageSize, HttpServletRequest request) {
        //分页信息
        Page<CommentOneVO> pageInfo = new Page<>(pageNumber, pageSize);

        //获取当前登录用户id
        Long userId = userUtil.getUserInfo(request).getId();

        //查询一级评论
        List<CommentOneVO> postCommentOnes = postCommentOneMapper.selectAllByPostId(pageInfo, postId, userId);

        //判断是否为空
        if (postCommentOnes.isEmpty()) return ResponseResult.COMMENT_NOT_FOUND;

        //填充isLike字段
        postCommentOnes.forEach((p) -> p.setIsLike(isLike(Values.LIKE_COMMENTS_PREFIX + p.getCommentId(), userId)));

        //返回数据
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("pageInfo", pageInfo);
        resMap.put("postCommentOne", postCommentOnes);

        return ResponseResult.OK.put(resMap);
    }

    @Override
    public ResponseResult<?> getCommentTow(Long commentId, Integer pageNumber, Integer pageSize, HttpServletRequest request) {
        //分页信息
        Page<CommentAllVO> pageInfo = new Page<>(pageNumber, pageSize);

        //获取当前登录用户id
        Long userId = userUtil.getUserInfo(request).getId();

        //查询二级评论
        List<CommentAllVO> postCommentAll = postCommentAllMapper.selectAllByCommentId(pageInfo, commentId, userId);

        //判断是否为空
        if (postCommentAll.isEmpty()) return ResponseResult.COMMENT_NOT_FOUND;

        //填充isLike字段
        postCommentAll.forEach((p) -> p.setIsLike(isLike(Values.LIKE_COMMENTS_PREFIX + -p.getCommentId(), userId)));

        //返回数据
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("pageInfo", pageInfo);
        resMap.put("postCommentAll", postCommentAll);

        return ResponseResult.OK.put(resMap);
    }

    /*
     true : 点赞
    false : 取消点赞
     */
    private boolean addLike(String key,Long userId){
        SetOperations<String, String> set = stringRedisTemplate.opsForSet();
        //判断用户是否点过赞
        Boolean member = set.isMember(key, String.valueOf(userId));
        if(member != null && member) { //存在，取消点赞
            set.remove(key, String.valueOf(userId));
            return false;
        } else { //不存在，添加点赞
            set.add(key, String.valueOf(userId));
            return true;
        }
    }

    private boolean isLike(String key,Long userId){
        SetOperations<String, String> set = stringRedisTemplate.opsForSet();
        Boolean member = set.isMember(key, String.valueOf(userId));
        return member!= null && member;
    }

    @Override
    public boolean deleteCommentByPostId(List<Long> postIds,HttpServletRequest request) {
        postIds.forEach((p)->{
            List<Integer> commentIds = postCommentOneMapper.selectIdByPostId(p);
            commentIds.forEach((c)->{
                deleteComment(Long.valueOf(c),false,request);
            });
        });
        return true;
    }
}




