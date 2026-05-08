package weilai.team.officialWebSiteApi.service.post.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;


import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import weilai.team.officialWebSiteApi.entity.admin.DO.User;
import weilai.team.officialWebSiteApi.entity.message.DO.Message;

import weilai.team.officialWebSiteApi.entity.post.DO.Post;

import weilai.team.officialWebSiteApi.entity.post.DTO.*;

import weilai.team.officialWebSiteApi.entity.post.VO.AdminPagePostVo;
import weilai.team.officialWebSiteApi.entity.post.VO.PagePostVo;
import weilai.team.officialWebSiteApi.entity.post.VO.PostVo;
import weilai.team.officialWebSiteApi.entity.post.VO.PutPostVo;
import weilai.team.officialWebSiteApi.mapper.admin.UserMapper;
import weilai.team.officialWebSiteApi.mapper.community.CommunityTagMapper;
import weilai.team.officialWebSiteApi.mapper.community.CommunityTagPostRelationsMapper;
import weilai.team.officialWebSiteApi.mapper.post.PostMapper;
import weilai.team.officialWebSiteApi.mapper.post.UserCollectMapper;
import weilai.team.officialWebSiteApi.mapper.postComment.PostCommentAllMapper;
import weilai.team.officialWebSiteApi.mapper.postComment.PostCommentOneMapper;
import weilai.team.officialWebSiteApi.service.community.impl.CommunityTagPostRelationsServiceImpl;
import weilai.team.officialWebSiteApi.service.message.sendMessage.MessageProducer;
import weilai.team.officialWebSiteApi.service.post.PostService;
import weilai.team.officialWebSiteApi.service.postComment.PostCommentService;
import weilai.team.officialWebSiteApi.util.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


/**
 * @author 杜昱徵
 * @description 针对表【post(用户发的贴子内容)】的数据库操作Service实现
 * @createDate 2024-11-11 17:01:02
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post>
        implements PostService {
    @Resource
    private PostMapper postMapper;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private UserUtil userUtil;
    @Resource
    private CommunityTagPostRelationsServiceImpl communityTagPostRelationsService;
    @Resource
    private CommunityTagPostRelationsMapper communityTagPostRelationsMapper;
    @Resource
    private CommunityTagMapper communityTagMapper;
    @Resource
    private PostCommentOneMapper commentOneMapper;
    @Resource
    private PostCommentAllMapper postCommentAllMapper;
    @Resource
    private UserCollectMapper userCollectMapper;
    @Resource
    private MessageProducer messageProducer;

    @Resource
    private UserMapper userMapper;
    @Resource
    private PostCommentService postCommentService;

    /**
     * 发布帖子
     *
     * @param postDto
     */
    @Transactional
    @Override
    public ResponseResult<?> put(PostDto postDto, HttpServletRequest request) {
        User userInfo = userUtil.getUserInfo(request);
        //如果用户为空则返回未登录
        if (userInfo == null) {
            return ResponseResult.Unauthorized;
        }
        //判断权限
        Set<String>auths=new HashSet<>(userInfo.getAuth());
        if(postDto.getType()==2&&!auths.contains("notice_admin")){
            return ResponseResult.FORBIDDEN;
        }
        String lockKey = Values.POST_CREATE_LOCK + userInfo.getId();
        try {
            // 使用锁防止重复提交
            if (!redisUtil.tryLock(lockKey, 5, TimeUnit.SECONDS)) {
                return ResponseResult.OPERATE_TOO_FREQUENT;
            }
            if (postDto.getType() != 1 && postDto.getType() != 2 && postDto.getType() != 3 && postDto.getType() != 4) {
                return ResponseResult.TYPE_NOT_EXIST;
            }
            Post post = new Post();
            if (postDto.getPostAbstract() == null || postDto.getPostAbstract().length() == 0) {
                //如果帖子的总字数小于100，则将帖子的总字数作为概要
                if (postDto.getPostTxt().length() <= 100) {
                    postDto.setPostAbstract(postDto.getPostTxt());
                } else {
                    //如果前端未传来帖子概要则取帖子的前100个字作为概要
                    postDto.setPostAbstract(postDto.getPostTxt().substring(0, 100));
                }
            } else if (postDto.getPostAbstract().length() > 100) {
                //如果概要大于100个字，则截取前100个字作为概要
                postDto.setPostAbstract(postDto.getPostAbstract().substring(0, 100));
            }
            //用BeanUtils 将postDto中的属性复制到post中
            BeanUtils.copyProperties(postDto, post);
            post.setPostTime(new Date());
            //获取用户id
            post.setUserId(userInfo.getId());
            int row = postMapper.insert(post);
            Long postId = post.getId();
            if (postDto.getType() != 2) {
                //添加标签
                String[] tags = postDto.getTags();
                ResponseResult<?> responseResult = communityTagPostRelationsService.addTagById(postId, tags);
                //如果添加标签失败，返回添加发布失败
                if (responseResult.getCode() == 5005) {
                    return ResponseResult.ADD_FAIL;
                }
            }
            if (postDto.getType() == 2) {
                PostNoticeDto postNoticeDto = new PostNoticeDto();
                postNoticeDto.setTitle(post.getTitle());
                postNoticeDto.setContent(post.getPostTxt());
                postNoticeDto.setUserId(post.getUserId());
                postNoticeDto.setCreateAt(post.getPostTime());
                postNoticeDto.setNoticeId(postId);
                messageProducer.sendToAll(postNoticeDto, request);
            }
            // 异步清理用户帖子缓存
            asyncClearUserCache(userInfo.getId());
            return row > 0 ? ResponseResult.POST_PUT_SUCCESS.put(new PutPostVo(postId)) : ResponseResult.POST_PUT_FAIL;
        } finally {
            redisUtil.unlock(lockKey);
        }
    }
    /**
     * 批量软删除帖子
     *
     * @param ids
     */
    @Override
    @Transactional
    public ResponseResult<?> deletePostByIds(List<Long> ids, HttpServletRequest request) {
        int row = 0;
        User userInfo = userUtil.getUserInfo(request);
        //如果用户为空则返回未登录
        if (userInfo == null) {
            return ResponseResult.Unauthorized;
        }
        //得到post对象
        for (Long id : ids) {
            Post post = postMapper.selectPostById(id);
            if (post == null) {
                continue;
            }
            if (!post.getUserId().equals(userInfo.getId())) {
                Message message = new Message(userInfo.getId(), post.getUserId(), id, 5, "管理员删除了您发布的博文");
                messageProducer.sendToUser(message, request);
            }
            //2.删除帖子对应的标签
            communityTagPostRelationsService.deleteByPostId(id);
            //进行软删除0为正常1为被删除
            post.setDeleteFlag(1);
            //保存到数据库
            row = postMapper.updateById(post);
            if (row > 0) {
                //删除帖子的评论
                postCommentService.deleteCommentByPostId(ids, request);
                userCollectMapper.deleteByPost(id);
                if (redisUtil.isCollect(id, request)) {
                    redisUtil.removeAllCollect(Values.IS_COLLECT__KEY + id);
                }
                if (redisUtil.isLike(id, request)) {
                    redisUtil.deleteRedis(Values.USER_POST_PREFIX + post.getUserId());
                }
            }
        }
        return row > 0 ? ResponseResult.POST_DELETE_SUCCESS : ResponseResult.POST_DELETE_FAIL;
    }

    /**
     * 查看帖子的详细信息
     *
     * @param id
     * @return
     */
    @Override
    public ResponseResult<?> getOnePost(Long id, HttpServletRequest request) {
        // 从 Redis 获取帖子详情
        PostVo postVo = redisUtil.getRedisObject(Values.KEY_POST_VO + id, PostVo.class);
        //如果不存在就查询数据库
        if (postVo==null) {
            boolean tryLock = redisUtil.tryLock(Values.POST_LIKE_LOCK + id, 5, TimeUnit.SECONDS);
            try {
                if(tryLock){
                    //获取一条帖子的详细信息
                    postVo = postMapper.getById(id);
                    if (postVo == null) {
                        // 缓存空结果，防止缓存穿透
                        redisUtil.setRedisObjectWithOutTime(Values.KEY_POST_VO + id, null, Values.NULL_CACHE_EXPIRE_TIME);
                        return ResponseResult.POST_ID_ISNULL;
                    }
                    //设置有效期为3天
                    redisUtil.setRedisObjectWithOutTime(Values.KEY_POST_VO + id, postVo, Values.POST_OUT_TIME);
                }
            } finally{
            redisUtil.unlock(Values.POST_LIKE_LOCK + id);
            }
        }
        //如果浏览量不存在可能是redis数据丢失，则查询数据库重新设置浏览量
        if(redisUtil.getViewCount(id)==0){
            int viewCountById = postMapper.selectViewCountById(id);
            redisUtil.incrementViewCount(id, viewCountById); // 修正参数顺序
        }
        // 更新浏览量（Redis 原子递增）
        redisUtil.incrementViewCount(id);
        Integer viewCount = redisUtil.getViewCount(id);
        postVo.setViewCount(viewCount);
        //添加用户头像和昵称
       User user = userMapper.selectById(postVo.getUserId());
        postVo.setHeadPortrait(user.getHeadPortrait());
        postVo.setName(user.getName());
        updateViewCount(viewCount,id);
        //获取评论数
        //获取评论数
        Integer commentCount = getCommentCountFromCache(id);
        postVo.setCommentCount(commentCount);
        //判断是否点赞
        Boolean isLike = redisUtil.isLike(id, request);
        //判断是否收藏
        Boolean isCollect = redisUtil.isCollect(id, request);
        //显示标签
        List<String> tags = this.getPostTagsFromCache(id);
        postVo.setPostTags(tags);
        //查询收藏数
        Integer collectCount = redisUtil.getCollectCount(Values.IS_COLLECT__KEY + id);
        postVo.setIsLike(isLike);
        postVo.setIsCollect(isCollect);
        postVo.setCollectCount(collectCount);
        return ResponseResult.GET_POST_SUCCESS.put(postVo);
    }

    /**
     * 根据id修改帖子的信息
     *
     * @param postDto
     */
    @Override
    @Transactional
    public ResponseResult<?> updatePostById(UpdatePostDto postDto, HttpServletRequest request) {
        User userInfo = userUtil.getUserInfo(request);
        //如果用户为空则返回未登录
        if (userInfo == null) {
            return ResponseResult.Unauthorized;
        }
        //查询是否为发布者如果不是则返回修改失败
        Post post1 = postMapper.selectPostById(postDto.getId());
        if (!post1.getUserId().equals(userInfo.getId())) {
            return ResponseResult.UPDATE_FAIL;
        }
        //修改标签
        //1.查询修改传过来的标签
        List<String> postTags = postDto.getPostTags();
        //2.删除帖子对应的标签
        communityTagPostRelationsService.deleteByPostId(postDto.getId());
        //3.添加新的标签
        if (postTags != null) {
            String[] tags = postTags.toArray(new String[0]);
            communityTagPostRelationsService.addTagById(postDto.getId(), tags);
        }
        //如果修改了帖子则更新一下redis缓存
        redisUtil.deleteRedis(Values.POST_TAGS_KEY + postDto.getId());
        redisUtil.deleteRedis(Values.KEY_POST_VO + postDto.getId());
        //创建一个post对象
        Post post = new Post();
        //将postDto中的属性复制到post中
        BeanUtils.copyProperties(postDto, post);
        redisUtil.deleteRedis(Values.USER_POST_PREFIX + post.getUserId());
        return postMapper.updateById(post) > 0 ? ResponseResult.UPDATE_SUCCESS : ResponseResult.UPDATE_FAIL;
    }

    /**
     * 分页查询帖子列表
     *
     * @param pageQueryDto
     */
    @Override
    public ResponseResult<?> pageQuery(PageQueryDto pageQueryDto, HttpServletRequest request) {
        // 如果类型为空则默认为综合
        if (pageQueryDto.getType() == null) {
            pageQueryDto.setType(0);
        }
        //如果类型不为1，3，4则显示不存在改类型
        if (pageQueryDto.getType() != 0 && pageQueryDto.getType() != 1 && pageQueryDto.getType() != 3 && pageQueryDto.getType() != 4) {
            return ResponseResult.TYPE_NOT_EXIST;
        }
               Page<PagePostVo>  page = new Page<>(pageQueryDto.getPage(), pageQueryDto.getPageSize());
                //如果类型为0则不传类型为综合
                if (pageQueryDto.getType() == 0) {
                    pageQueryDto.setType(null);
                }
                // 查询到的该页数据
                List<PagePostVo> postVos;
                if (pageQueryDto.getSort() == 1) {
                    postVos = postMapper.selectPostByConditions(page, pageQueryDto.getType(), pageQueryDto.getCondition(), pageQueryDto.getStartTime(), pageQueryDto.getEndTime());
                } else {
                    //显示热门的帖子
                    postVos = postMapper.selectPostsByConditions(page, pageQueryDto.getType(), pageQueryDto.getCondition(), pageQueryDto.getStartTime(), pageQueryDto.getEndTime());
                }
                for (PagePostVo postVo : postVos) {
                    //添加评论数
                    //获取评论数
                    Integer commentCount = getCommentCountFromCache(postVo.getId());
                    postVo.setCommentCount(commentCount);
                    //查寻点赞数
                    //判断是否点赞
                    Boolean like = redisUtil.isLike(postVo.getId(), request);
                    postVo.setIsLike(like);
                    //显示标签
                    //显示标签
                    List<String> tags = this.getPostTagsFromCache(postVo.getId());
                    postVo.setPostTags(tags);
                }
                page.setRecords(postVos);
                return ResponseResult.GET_POST_SUCCESS.put(page);
    }
    /**
     * 对帖子进行点赞
     *
     * @param id
     */
    @Override
    public ResponseResult<?> like(Long id, HttpServletRequest request) {
        // 获取用户信息
        User userInfo = userUtil.getUserInfo(request);
        if (userInfo == null) {
            return ResponseResult.Unauthorized;
        }
        // 判断帖子是否存在
        Post post = postMapper.selectPostById(id);
        if (post == null) {
            return ResponseResult.POST_ID_ISNULL;
        }
        // 获取 Redis 中的点赞集合 Key
        String likedKey = Values.KEY_LIKED + id;
        // 使用 Lua 脚本实现原子性操作
        String luaScript = "local isLiked = redis.call('SISMEMBER', KEYS[1], ARGV[1])\n" +
                "if isLiked == 0 then\n" +
                "    redis.call('SADD', KEYS[1], ARGV[1])\n" +
                "    redis.call('INCR', KEYS[2])\n" +
                "    return 1\n" +
                "else\n" +
                "    redis.call('SREM', KEYS[1], ARGV[1])\n" +
                "    redis.call('DECR', KEYS[2])\n" +
                "    return 0\n" +
                "end";
        Long userId = userInfo.getId();
        List<String> keys = Arrays.asList(likedKey, Values.LIKE_COUNT + id);
        // 执行 Lua 脚本
        Object result = redisUtil.executeLua(luaScript, keys, Collections.singletonList(userId.toString()));
        try {
            if(redisUtil.isExist(Values.KEY_POST_VO+id)){
                redisUtil.deleteRedis(Values.KEY_POST_VO+id);
            }
            if ((Long) result == 1) {
                // 点赞成功
                asyncUpdateLikeCount(post, true); // 异步更新数据库
                // 发送点赞通知
                Message message = new Message(userId, post.getUserId(), id, 1, null);
                messageProducer.sendToUser(message, request); // 异步发送消息
                return ResponseResult.POST_LIKE_SUCCESS;
            } else {
                // 取消点赞成功
                asyncUpdateLikeCount(post, false); // 异步更新数据库
                return ResponseResult.UN_LIKE_SUCCESS;
            }
        } catch (Exception e) {
            log.error("点赞操作失败", e);
            return ResponseResult.SERVICE_ERROR;
        }
    }
    /**
     * 删除单个帖子
     *
     * @param id
     * @param request
     * @return
     */
    @Transactional
    @Override
    public ResponseResult<?> deletePostById(Long id, HttpServletRequest request) {
        User userInfo = userUtil.getUserInfo(request);
        //如果用户为空则返回未登录
        if (userInfo == null) {
            return ResponseResult.Unauthorized;
        }
        //得到post对象
        Post post = postMapper.selectPostById(id);
        if (post == null) {
            return ResponseResult.POST_ID_ISNULL;
        }
        //判断是否为帖子的发布者 如果不是则无法删除
        if (!post.getUserId().equals(userInfo.getId())) {
            return ResponseResult.POST_DELETE_FAIL;
        }
        //2.删除帖子对应的标签
        communityTagPostRelationsService.deleteByPostId(id);
        //进行软删除0为正常1为被删除
        post.setDeleteFlag(1);
        //保存到数据库
        int row = postMapper.updateById(post);
        if (row >= 0) {
            //删除帖子的评论
            postCommentService.deleteCommentByPostId(Collections.singletonList(id), request);
            userCollectMapper.deleteByPost(id);
            redisUtil.removeAllCollect(Values.IS_COLLECT__KEY + id);
            redisUtil.deleteRedis(Values.USER_POST_PREFIX + post.getUserId());
        }
        return row > 0 ? ResponseResult.POST_DELETE_SUCCESS : ResponseResult.POST_DELETE_FAIL;
    }

    /**
     * 根据id集合查询帖子
     *
     * @param pageQueryDto
     * @param request
     * @return
     */
    @Override
    public Page<PagePostVo> selectPostByIds(PageDto pageQueryDto, HttpServletRequest request) {
        Page<PagePostVo> page = new Page(pageQueryDto.getPage(), pageQueryDto.getPageSize());
        List<PagePostVo> postVos = postMapper.selectPostsByIds(page, pageQueryDto.getIds());
        for (PagePostVo postVo : postVos) {
            //添加评论数
            //添加评论数
            //获取评论数
            Integer commentCount = getCommentCountFromCache(postVo.getId());
            postVo.setCommentCount(commentCount);
            //判断是否收藏
            Boolean collect = redisUtil.isCollect(postVo.getId(), request);
            //查询收藏数
            Integer collectCount = redisUtil.getCollectCount(Values.IS_COLLECT__KEY + postVo.getId());
            //判断是否点赞
            Boolean like = redisUtil.isLike(postVo.getId(), request);
            postVo.setIsLike(like);
            //显示标签
            //显示标签
            List<String> tags = this.getPostTagsFromCache(postVo.getId());
            postVo.setPostTags(tags);
        }
        page.setRecords(postVos);
        return page;
    }

    @Override
    public ResponseResult<?> AdminPageQuery(PageQueryDto pageQueryDto, HttpServletRequest request) {
        // 如果类型为空则默认为综合
        if (pageQueryDto.getType() == null) {
            pageQueryDto.setType(0);
        }
                Page<AdminPagePostVo> page = new Page<>(pageQueryDto.getPage(), pageQueryDto.getPageSize());
                //如果类型为0则不传类型为综合
                if (pageQueryDto.getType() == 0) {
                    pageQueryDto.setType(null);
                }
                // 查询到的该页数据
           List<AdminPagePostVo> postVos = postMapper.selectAdminPosts(page, pageQueryDto.getType(), pageQueryDto.getCondition(), pageQueryDto.getStartTime(), pageQueryDto.getEndTime());
                page.setRecords(postVos);
                return ResponseResult.GET_POST_SUCCESS.put(page);
    }

    /**
     * 缓存标签信息
     *
     * @param postId
     * @return
     */
    private List<String> getPostTagsFromCache(Long postId) {
        String cacheKey = Values.POST_TAGS_KEY + postId;
        if (redisUtil.isExist(cacheKey)) {
            return (List<String>) redisUtil.getRedisObject(cacheKey, List.class);
        }
        List<Long> tagIds = communityTagPostRelationsMapper.findTagIds(postId);
        List<String> tagNames = new ArrayList<>();
        for (Long tagId : tagIds) {
            String tagName = communityTagMapper.findById(tagId);
            tagNames.add(tagName);
        }
        redisUtil.setRedisObjectWithOutTime(cacheKey, tagNames, 3600000L); // 缓存1小时
        return tagNames;
    }

    /**
     * 缓存评论数
     *
     * @param postId
     * @return
     */
    private Integer getCommentCountFromCache(Long postId) {
        String cacheKey = Values.COMMENT_COUNT_KEY + postId;
        if (redisUtil.isExist(cacheKey)) {
            return (Integer) redisUtil.getRedisObject(cacheKey, Integer.class);
        }
        //获取评论数
        //多级评论数
        Integer commentCount = postCommentAllMapper.selectCommentCount(postId);
        if (commentCount == null) {
            commentCount = 0;
        }
        //一级评论数
        Integer OneCount = commentOneMapper.selectCountByPostId(postId);
        if (OneCount == null) {
            OneCount = 0;
        }
        redisUtil.setRedisObjectWithOutTime(cacheKey, commentCount + OneCount, 3600000L); // 缓存1小时
        return commentCount;
    }


    @Async
    protected void asyncClearUserCache(Long userId) {
        redisUtil.deleteRedis(Values.USER_POST_PREFIX + userId);
    }

    /**
     * 异步更新数据库浏览量
     * @param viewCount
     * @param postId
     */
    @Async
    protected void updateViewCount(Integer viewCount,Long postId){
        postMapper.updateViewCountById(viewCount,postId);
    }

    /**
     * 异步更新数据库点赞量
     * @param post
     * @param isLike
     * @return
     */
    private void asyncUpdateLikeCount(Post post, boolean isLike) {
        CompletableFuture.runAsync(() -> {
            int retryCount = 3; // 最大重试次数
            while (retryCount > 0) {
                try {
                    if (isLike) {
                        postMapper.incrementLikeCount(post.getId());
                        break;
                    } else {
                        postMapper.descrementLikeCount(post.getId());
                        break;
                    }
                } catch (Exception e) {
                    retryCount--;
                    if (retryCount == 0) {
                        log.error("更新数据库失败，已达到最大重试次数", e);
                    }
                }
            }
        });
    }
}
