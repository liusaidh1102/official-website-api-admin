package weilai.team.officialWebSiteApi.controller.community;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import weilai.team.officialWebSiteApi.entity.admin.DO.User;
import weilai.team.officialWebSiteApi.entity.community.DTO.TagPageDto;
import weilai.team.officialWebSiteApi.service.admin.annotation.AutoLog;
import weilai.team.officialWebSiteApi.service.community.CommunityTagPostRelationsService;
import weilai.team.officialWebSiteApi.service.community.CommunityTagService;
import weilai.team.officialWebSiteApi.util.ResponseResult;
import weilai.team.officialWebSiteApi.util.UserUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * className tagController
 *
 * @Author yrx
 * @Description //TODO 标签功能的实现
 */
@RestController
@RequestMapping("community_tag")
//@CrossOrigin
@Api(tags = "标签模块")
public class tagController {
    @Resource
    CommunityTagService communityTagService;
    @Resource
    CommunityTagPostRelationsService communityTagPostRelationsService;
    @Resource
    UserUtil userUtil;

    /**
     * 根据帖子的id给帖子添加标签
     * @param PostId 帖子id
     * @param TagNames 标签名称
     * @return  添加标签成功的信息
     */
    @AutoLog
    @ApiOperation("根据帖子的id给帖子添加标签")
    @PostMapping("/addTagById")
    public ResponseResult<?> addTagById(Long PostId, @RequestParam String[]TagNames ){
        return communityTagPostRelationsService.addTagById(PostId,TagNames);
    }

    /**
     * 根据帖子的id和要删除的标签的名称来删除文章的标签
     * @param PostId  帖子id
     * @param TagName  标签名称
     * @return 删除成功的信息
     */
    @AutoLog
    @ApiOperation("根据帖子的id和要删除的标签的名称来删除文章的标签")
    @DeleteMapping("/deleteByIdAndTagName")
    public ResponseResult<?> deleteByIdAndTagName(Long PostId,String TagName){
        return communityTagPostRelationsService.deleteByIdAndTagName(PostId,TagName);
    }

    /**
     * 根据用户使用过的标签进行推荐
     * @return  推荐的标签
     */
    @AutoLog
    @ApiOperation("根据用户使用过的标签进行推荐")
    @GetMapping("/recommend")
    public ResponseResult<?> recommend(Integer type,HttpServletRequest request){
        User userInfo =userUtil.getUserInfo(request);
        if (userInfo!=null) {
            Long uid = userInfo.getId();
            return  communityTagPostRelationsService.recommendByUserId(type,uid);
        }else{
            return ResponseResult.Unauthorized;
        }

    }

    /**
     * 热门标签展示
     * @return 热门标签
     */
    @AutoLog
    @ApiOperation("热门标签展示")
    @GetMapping("/hotTags")
    public ResponseResult<?> findHotTagsByTagUses(Integer type){
        return communityTagService.findHotTagsByTagUses(type);
    }

    /**
     * 标签的云展示
     */
    @AutoLog
    @ApiOperation("标签的云展示")
    @GetMapping("/getTagNames")
    public ResponseResult<?>getTagNames(){
        return communityTagService.getAllTagNames();
    }

    /**
     * 关联帖子列表
     * @param
     * @return 使用该标签名称的所有帖子
     */
    @AutoLog
    @ApiOperation("关联帖子列表")
    @GetMapping("/relativePost")
    public ResponseResult<?>findPostByTagName(TagPageDto pageDto,Integer type, HttpServletRequest request){
        return communityTagPostRelationsService.findPostByTagName(pageDto,type,request);
    }

    /**
     * 标签筛选
     * @param str 匹配的字符串
     * @return
     */
    @AutoLog
    @ApiOperation("标签筛选")
    @GetMapping("/LabelFiltering")
    public ResponseResult<?>LabelFiltering(String str){
        return communityTagPostRelationsService.labelFiltering(str);
    }

    /**
     * 标签的使用统计
     */
    @AutoLog
    @ApiOperation("标签的使用统计")
    @GetMapping("/UsageStatistics")
    public ResponseResult<?>UsageStatistics(Integer type){
        return communityTagPostRelationsService. getTagUsageStatistics(type);
    }
}
