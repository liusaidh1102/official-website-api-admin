package weilai.team.officialWebSiteApi.mapper.postComment;
import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import weilai.team.officialWebSiteApi.entity.postComment.DO.PostCommentAll;
import weilai.team.officialWebSiteApi.entity.postComment.VO.CommentAllVO;

/**
 * ClassName:PostCommentAllMapper
 * Description:
 *
 * @Author:独酌
 * @Create:2024/11/15 17:38
 */
@Mapper
public interface PostCommentAllMapper extends BaseMapper<PostCommentAll> {

    /**
     * 根据一级评论的id查询二级评论
     * @param pageInfo  分页信息
     * @param commentId 一级评论的id
     * @param userId    当前用户的id
     * @return 二级评论
     */
    List<CommentAllVO> selectAllByCommentId(Page<CommentAllVO> pageInfo, @Param("commentId") Long commentId, @Param("userId") Long userId);

    /**
     * 根据一级评论id删除二级评论
     * @param deleteFlag 删除标志 0 未删除 1 删除
     * @param commentId 一级评论id
     * @return 影星数据库的行数
     */
    int updateDeleteFlagByCommentId(@Param("deleteFlag") Integer deleteFlag, @Param("commentId") Long commentId);

    /**
     * 根据一级评论的id查询所有的二级评论id
     * @param commentId 一级评论的id
     * @return 所有的二级评论id
     */
    List<Long> selectIdByCommentId(@Param("commentId") Long commentId);

    /**
     *查询多级评论的评论数
     * @param postId 文章id
     * @return 评论数
     */
    Integer selectCommentCount(Long postId);

    /**
     * 更新评论的点赞数
     * @param likeCount 点赞数
     * @param id 评论的id
     * @return 影响的行数
     */
    int updateLikeCountById(@Param("likeCount") long likeCount, @Param("id") Long id);

    /**
     * 定时清理软删除
     * @return 影响的行数
     */
    int softDeleteClear();

}
