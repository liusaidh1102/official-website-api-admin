package weilai.team.officialWebSiteApi.mapper.postComment;
import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import weilai.team.officialWebSiteApi.entity.postComment.DO.PostCommentOne;
import weilai.team.officialWebSiteApi.entity.postComment.VO.CommentOneVO;

/**
 * ClassName:PostCommentOneMapper
 * Description:
 *
 * @Author:独酌
 * @Create:2024/11/15 17:37
 */
@Mapper
public interface PostCommentOneMapper extends BaseMapper<PostCommentOne> {

    /**
     * 根据贴子的id获取该帖子的一级评论
     * @param postId 贴子的id
     * @param userId 当前用户的id
     * @return 该贴子的一级评论
     */
    List<CommentOneVO> selectAllByPostId(Page<CommentOneVO> pageInfo,@Param("postId") Long postId, @Param("userId") Long userId);

    /**
     * 查询评论数量
     * @param id 贴子的id
     * @return 评论数量
     */
    Integer selectCountByPostId(Long id);

    /**
     * 查询一级评论的贴子id
     * @param commentId 一级评论的id
     */
    Integer selectPostId(Long commentId);

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


    /**
     * 根据贴子id查询一级评论的id
     */
    List<Integer> selectIdByPostId(@Param("postId") Long postId);
}
