package weilai.team.officialWebSiteApi.controller.post;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import weilai.team.officialWebSiteApi.entity.post.DTO.PageQueryDto;
import weilai.team.officialWebSiteApi.service.admin.annotation.AutoLog;
import weilai.team.officialWebSiteApi.service.post.PostService;
import weilai.team.officialWebSiteApi.util.ResponseResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/admin_post")
@Slf4j
@Api(tags = "帖子后台管理")
public class AdminPostController {
    @Resource
    private PostService postService;

    @AutoLog
    @ApiOperation("后台管理的分页查询帖子列表")
    @GetMapping("/selectAll")
    public ResponseResult<?> selectAll(PageQueryDto pageQueryDto, HttpServletRequest request) {
        log.info("分页查询帖子");
        return postService.AdminPageQuery(pageQueryDto, request);
    }


    @AutoLog
    @ApiOperation("批量删除帖子")
    @PutMapping("/deletes/{ids}")
    public ResponseResult<?> deleteById(@PathVariable List<Long> ids, HttpServletRequest request) {
        log.info("要删除帖子的id为：{}", ids);
        return postService.deletePostByIds(ids, request);
    }

}
