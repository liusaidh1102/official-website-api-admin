package weilai.team.officialWebSiteApi.controller.postComment;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import weilai.team.officialWebSiteApi.entity.postComment.DTO.ReplyCommentDTO;
import weilai.team.officialWebSiteApi.entity.postComment.DTO.WritePostCommentDTO;
import weilai.team.officialWebSiteApi.service.admin.annotation.AutoLog;
import weilai.team.officialWebSiteApi.service.postComment.PostCommentService;
import weilai.team.officialWebSiteApi.util.ResponseResult;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * ClassName:CommentController
 * Description:
 *
 * @Author:独酌
 * @Create:2024/11/15 15:04
 */
@RestController
@Api(tags = "评论相关操作",description = " ")
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private PostCommentService  postCommentService;


    @AutoLog
    @PostMapping("/writePostComment")
    @ApiOperation("写一级评论")
    public ResponseResult<?> writePostComment(@Valid @RequestBody WritePostCommentDTO writePostCommentDTO, HttpServletRequest request){
        return postCommentService.writePostComment(writePostCommentDTO,request);
    }

    @AutoLog
    @PostMapping("/replyComment")
    @ApiOperation("写多级评论")
    public ResponseResult<?> replyComment(@Valid @RequestBody ReplyCommentDTO replyCommentDTO,HttpServletRequest request){
        return postCommentService.replyComment(replyCommentDTO,request);
    }


    @AutoLog
    @DeleteMapping("/deleteComment/{commentId}")
    @ApiOperation("删除评论")
    @ApiImplicitParam(name = "commentId",value = "评论id[一级评论 > 0、多级评论 < 0]",required = true,dataType = "string")
    public ResponseResult<?> deleteComment(@PathVariable String commentId, HttpServletRequest request){
        return postCommentService.deleteComment(Long.valueOf(commentId),true,request);
    }

    @AutoLog
    @PutMapping("/likeOption/{commentId}")
    @ApiOperation("评论--点赞/取消")
    @ApiImplicitParam(name = "commentId",value = "评论id[一级评论 > 0、多级评论 < 0]",required = true,dataType = "string")
    public ResponseResult<?> likeOption(@PathVariable String commentId,HttpServletRequest request){
        return postCommentService.likeOption(Long.valueOf(commentId),request);
    }

    @AutoLog
    @GetMapping("/getCommentOne")
    @ApiOperation("指定贴子下，获取一级评论")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "postId",value = "贴子id",required = true,dataType = "string"),
        @ApiImplicitParam(name = "pageNumber",value = "显示页码",required = true,dataType = "string"),
        @ApiImplicitParam(name = "pageSize",value = "每页条数",required = true,dataType = "string")
    })
    public ResponseResult<?> getCommentOne(String postId, Integer pageNumber, Integer pageSize,HttpServletRequest request){
        return postCommentService.getCommentOne(Long.valueOf(postId),pageNumber,pageSize,request);
    }

    @AutoLog
    @GetMapping("/getCommentTwo")
    @ApiOperation("指定一级评论下，获取二级评论")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "commentId",value = "一级评论的id",required = true,dataType = "string"),
            @ApiImplicitParam(name = "pageNumber",value = "显示页码",required = true,dataType = "string"),
            @ApiImplicitParam(name = "pageSize",value = "每页条数",required = true,dataType = "string")
    })
    public ResponseResult<?> getCommentTwo(String commentId, Integer pageNumber, Integer pageSize,HttpServletRequest request){
        return postCommentService.getCommentTow(Long.valueOf(commentId),pageNumber,pageSize,request);
    }
}
