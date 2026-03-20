package weilai.team.officialWebSiteApi.controller.post;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import weilai.team.officialWebSiteApi.entity.post.DTO.PageDto;
import weilai.team.officialWebSiteApi.entity.post.DTO.PageQueryDto;
import weilai.team.officialWebSiteApi.entity.post.DTO.PostDto;
import weilai.team.officialWebSiteApi.entity.post.DTO.UpdatePostDto;
import weilai.team.officialWebSiteApi.service.admin.annotation.AutoLog;
import weilai.team.officialWebSiteApi.service.admin.annotation.CurrentLimiting;
import weilai.team.officialWebSiteApi.service.post.PostService;
import weilai.team.officialWebSiteApi.service.post.UserCollectService;
import weilai.team.officialWebSiteApi.util.ResponseResult;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/post")
@Slf4j
@Api(tags = "帖子管理")
public class PostController {
    @Resource
    private PostService postService;
    @Resource
    private UserCollectService collectService;


    @AutoLog
    @CurrentLimiting
    @ApiOperation("发表帖子")
    @PostMapping("/put")
    public ResponseResult<?> put(@RequestBody PostDto postDto, HttpServletRequest request) {
        log.info("发布帖子");
        return postService.put(postDto, request);
    }


    @AutoLog
     @ApiOperation("删除帖子")
    @PutMapping("/delete/{id}")
    public ResponseResult<?> deleteById(@PathVariable Long id, HttpServletRequest request) {
        log.info("要删除帖子的id为：{}", id);
        return postService.deletePostById(id, request);
    }

    @AutoLog
    @ApiOperation("查询单个帖子的详细信息")
    @GetMapping("/selectOne/{id}")
    public ResponseResult<?> selectOne(@PathVariable Long id, HttpServletRequest request) {
        log.info("要查询帖子的id为：{}", id);
        return postService.getOnePost(id, request);
    }

    @AutoLog
    @ApiOperation("修改帖子")
    @PutMapping("/updatePost/{id}")
    public ResponseResult<?> updatePostById(@RequestBody UpdatePostDto postDto, HttpServletRequest request) {
        log.info("要修改帖子的id为：{}", postDto.getId());
        return postService.updatePostById(postDto, request);
    }

    @AutoLog
    @ApiOperation("分页查询帖子")
    @GetMapping("/selectAll")
    public ResponseResult<?> selectAll(PageQueryDto pageQueryDto, HttpServletRequest request) {
        log.info("分页查询帖子");
        return postService.pageQuery(pageQueryDto, request);
    }

    @AutoLog
    @ApiOperation("对帖子进行点赞")
    @PutMapping("/like/{id}")
    public ResponseResult<?> like(@PathVariable Long id, HttpServletRequest request) {
        log.info("对帖子点赞，帖子的id为：{}", id);
        return postService.like(id, request);
    }

    @AutoLog
    @ApiOperation("对帖子收藏")
    @PostMapping("/collect/{id}")
    public ResponseResult<?> collect(@PathVariable Long id, HttpServletRequest request) {
        log.info("收藏帖子，帖子的id为：{}", id);
        return collectService.collect(id, request);
    }

    @AutoLog
    @ApiOperation("根据id集合查询帖子")
    @GetMapping("/selectPosts")
    public void selectPostByIds(PageDto pageQueryDto, HttpServletRequest request){
        log.info("查询帖子，查询条件为：{}", pageQueryDto);
        postService.selectPostByIds(pageQueryDto,request);
    }
}
