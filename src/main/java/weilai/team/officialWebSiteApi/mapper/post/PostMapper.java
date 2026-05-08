package weilai.team.officialWebSiteApi.mapper.post;
import java.util.Collection;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import weilai.team.officialWebSiteApi.entity.post.DO.Post;

import weilai.team.officialWebSiteApi.entity.post.VO.AdminPagePostVo;
import weilai.team.officialWebSiteApi.entity.post.VO.PagePostVo;
import weilai.team.officialWebSiteApi.entity.post.VO.PostVo;

import java.util.Date;
import java.util.List;

/**
* @author 35543
* @description 针对表【post(用户发的贴子内容)】的数据库操作Mapper
* @createDate 2024-11-11 17:01:02
* @Entity weilai.team.officialWebSiteApi.entity.post.Do.Post
*/
@Mapper
public interface PostMapper extends BaseMapper<Post> {
    /**
     * 查询帖子的详细信息
     * @param id
     * @return
     */
    @Select("select user_id,view_count,like_count,post_time,title,post_txt,post_abstract,type from post where id=#{id} and delete_flag=0 ")
    PostVo getById(Long id);

    /**
     * 分页查询帖子
     * @param
     * @return
     */
    List<PagePostVo> selectPostsByConditions(Page page, Integer type, String condition, Date startTime, Date endTime);
    /**
     * 修改访问量
     * @param viewCount
     * @param id
     */
    void updateViewCountById(Integer viewCount, Long id);

    /**
     * 查询帖子全部信息
     * @param id
     * @return
     */
    @Select("select * from post where id=#{id} and delete_flag=0")
    Post selectPostById(Long id);



    /**
     * 根据用户id查询用户曾发过的帖子id
     *
     * @param type
     * @param uid
     * @return
     */
    List<Long> selectPostIdsByUid(@Param("type") Integer type, @Param("uid") Long uid);

    /**
     * 根据帖子id查询帖子标题
     * @param postId
     * @return
     */
    @Select("select title from post where id=#{postId}")
    String selectPostTitle(Long postId);

    /**
     * 根据id集合查询帖子
     * @param page
     * @param ids
     * @return
     */
    List<PagePostVo> selectPostsByIds(Page<PagePostVo> page, List<Long> ids);

    /**
     * 根据条件查询最新帖子
     * @param page
     * @param type
     * @param condition
     * @return
     */
    List<PagePostVo> selectPostByConditions(Page<PagePostVo> page, Integer type, String condition, Date startTime, Date endTime);
   @Select("select view_count from post where id=#{id}")
    int selectViewCountById(Long id);


    /**
     * 根据帖子的id查类型
     * @return
     * @param postId
     */
    int selectTypeByPostId(Long postId);

    /**
     * 根据用户id删除用户对应的贴子
     * @param userIdList 用户id
     * @return 删除的数量
     */
    int deleteByUserIdIn(@Param("userIdList") Collection<Long> userIdList);

    /**
     * 管理员查询帖子
     * @param page
     * @param type
     * @param condition
     * @param startTime
     * @param endTime
     * @return
     */
    List<AdminPagePostVo> selectAdminPosts(Page<AdminPagePostVo> page, Integer type, String condition, Date startTime, Date endTime);

    /**
     * 清理被软删除的帖子
     * @return
     */
    int clearPost();

    /**
     * 点赞
     * @param id
     */
    void incrementLikeCount(Long id);

    /**
     * 取消点赞
     * @param id
     */
    void descrementLikeCount(Long id);
}




