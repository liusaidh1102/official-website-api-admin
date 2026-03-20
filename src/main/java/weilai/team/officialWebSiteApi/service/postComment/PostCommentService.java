package weilai.team.officialWebSiteApi.service.postComment;

import weilai.team.officialWebSiteApi.entity.postComment.DTO.ReplyCommentDTO;
import weilai.team.officialWebSiteApi.entity.postComment.DTO.WritePostCommentDTO;
import weilai.team.officialWebSiteApi.util.ResponseResult;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 王科林
* @description 针对表【post_comment_photo(评论的图片)】的数据库操作Service
* @createDate 2024-11-15 15:10:38
*/
public interface PostCommentService {

    /**
     * 一级评论
     * @param writePostCommentDTO 评论的信息
     * @param request 当前登录用户信息
     * @return 评论结果
     */
    ResponseResult<?> writePostComment(WritePostCommentDTO writePostCommentDTO, HttpServletRequest request);

    /**
     * 二级、多级评论
     * @param replyCommentDTO 回复信息
     * @param request 当前登录用户信息
     * @return 回复结果
     */
    ResponseResult<?> replyComment(ReplyCommentDTO replyCommentDTO, HttpServletRequest request);

    /**
     * 根据评论/回复的id删除评论/回复
     *
     * @param commentId 评论/回复的id
     * @param request 当前登录用户信息
     * @return 删除结果
     */
    ResponseResult<?> deleteComment(Long commentId,boolean isNotFromPost, HttpServletRequest request);

    /**
     * 评论点赞
     *
     * @param commentId 评论id
     * @param request 用户信息
     * @return 点赞结果
     */
    ResponseResult<?> likeOption(Long commentId, HttpServletRequest request);

    /**
     * 获取一级评论
     *
     * @param postId     贴子id
     * @param pageNumber 当前页码
     * @param pageSize   每页显示条数
     * @param request    用户信息
     * @return 一级评论
     */
    ResponseResult<?> getCommentOne(Long postId, Integer pageNumber, Integer pageSize, HttpServletRequest request);

    /**
     * 获取指定一级评论下的二级评论
     *
     * @param commentId  一级评论id
     * @param pageNumber 当前页码
     * @param pageSize   每页显示条数
     * @param request    用户信息
     * @return 一级评论下的二级评论
     */
    ResponseResult<?> getCommentTow(Long commentId, Integer pageNumber, Integer pageSize, HttpServletRequest request);

    /**
     * 根据贴子id删除贴子下所有的评论
     */
    boolean deleteCommentByPostId(List<Long> postIds,HttpServletRequest request);
}
