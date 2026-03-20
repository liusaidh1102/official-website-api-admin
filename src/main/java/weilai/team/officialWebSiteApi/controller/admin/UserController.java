package weilai.team.officialWebSiteApi.controller.admin;

import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import weilai.team.officialWebSiteApi.entity.admin.DTO.AutomaticUserCourseDTO;
import weilai.team.officialWebSiteApi.entity.admin.DTO.UpdateUserInfoDTO;
import weilai.team.officialWebSiteApi.entity.admin.DTO.UserCourseDTO;
import weilai.team.officialWebSiteApi.service.admin.UserService;
import weilai.team.officialWebSiteApi.service.admin.annotation.AutoLog;
import weilai.team.officialWebSiteApi.util.ResponseResult;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * ClassName:UserController
 * Description:
 *
 * @Author:独酌
 * @Create:2024/11/12 11:44
 */
@RestController
@Api(tags = "用户信息相关操作",description = " ")
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @AutoLog
    @GetMapping("/getUserInfoByUserId/{userId}")
    @ApiOperation("获取指定用户基本信息")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "userId",value = "用户id",paramType = "path",required = true)
    })
    public ResponseResult<?> getUserInfoByUserId(@PathVariable Long userId){
        return userService.getUserInfoByUserId(userId);
    }

    @AutoLog
    @GetMapping("/getUserLifePhotoByUserId/{userId}")
    @ApiOperation("获取指定用户的生活照片")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "userId",value = "用户id",paramType = "path",required = true)
    })
    public ResponseResult<?> getUserLifePhotoByUserId(@PathVariable Long userId){
        return userService.getUserLifePhotoByUserId(userId);
    }

    @AutoLog
    @PutMapping("/updateUserInfo")
    @ApiOperation("更新当前用户信息")
    public ResponseResult<?> updateUserInfo(@RequestBody UpdateUserInfoDTO updateUserInfoDTO, HttpServletRequest request){
        return userService.updateUserInfo(updateUserInfoDTO,request);
    }

    @AutoLog
    @PutMapping("/updateUserHeadPortrait")
    @ApiOperation("更新当前用户头像")
    @ApiImplicitParam(name = "headPortrait",value = "头像文件",required = true)
    public ResponseResult<?> updateUserHeadPortrait(MultipartFile headPortrait, HttpServletRequest request){
        return userService.updateUserHeadPortrait(headPortrait,request);
    }

    @AutoLog
    @PostMapping("/addUserLifePhoto")
    @ApiOperation("增加当前用户生活照片")
    @ApiImplicitParam(name = "lifePhoto",value = "生活照片文件",required = true)
    public ResponseResult<?> addUserLifePhoto(List<MultipartFile> lifePhoto, HttpServletRequest request){
        return userService.addUserLifePhoto(lifePhoto,request);
    }

    @AutoLog
    @DeleteMapping("/deleteUserLifePhoto")
    @ApiOperation("删除当前用户生活照片")
    @ApiImplicitParam(name = "url",value = "生活照片文件的url",required = true)
    public ResponseResult<?> deleteUserLifePhoto(String url,HttpServletRequest request){
        return userService.deleteUserLifePhoto(url,request);
    }

    @AutoLog
    @PostMapping("/addAutomaticUpdateUserCourse")
    @ApiOperation("添加当前用户的自动更新课表")
    public ResponseResult<?> addAutomaticUpdateUserCourse(Date startTime, MultipartFile courseHtml, HttpServletRequest request){
        if(startTime == null || courseHtml == null || courseHtml.isEmpty()) {
            return ResponseResult.Bad_Request;
        }

        return userService.addAutomaticUpdateUserCourse(new AutomaticUserCourseDTO(courseHtml,startTime),request);
    }

    @AutoLog
    @GetMapping("/getUserCourse/{userId}")
    @ApiOperation("获取指定用户的课表")
    public ResponseResult<?> getUserCourse(@PathVariable String userId){
        return userService.getUserCourse(Long.parseLong(userId));
    }

    @AutoLog
    @PostMapping("/addUserCourse")
    @ApiOperation("添加当前用户的课表")
    public ResponseResult<?> addUserCourse(@Valid @RequestBody UserCourseDTO userCourseDTO, HttpServletRequest request) {
        return userService.setUserCourse(userCourseDTO,-1L,request);
    }

    @AutoLog
    @DeleteMapping("/deleteUserCourse/{oneCourseId}")
    @ApiOperation("删除当前用户的某个课程")
    @ApiImplicitParam(name = "oneCourseId",value = "课程id",required = true)
    public ResponseResult<?> deleteUserCourse(@PathVariable Long oneCourseId,HttpServletRequest request) {
        return userService.deleteUserCourse(oneCourseId,request);
    }

    @AutoLog
    @PutMapping("/updateUserCourse")
    @ApiOperation("更新当前用户的某个课程")
    public ResponseResult<?> updateUserCourse(@Valid @RequestBody UserCourseDTO userCourseDTO,HttpServletRequest request) {
        return userService.updateUserCourse(userCourseDTO,request);
    }

    @AutoLog
    @GetMapping("/getUserPost")
    @ApiOperation("获取指定用户的贴子")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "用户id",required = true),
            @ApiImplicitParam(name = "pageNumber",value = "显示页码",required = true),
            @ApiImplicitParam(name = "pageSize",value = "每页条数",required = true)
    })
    public ResponseResult<?> getUserPost(Long userId,Integer pageNumber,Integer pageSize){
        return userService.getUserPost(userId,pageNumber,pageSize);
    }

    @AutoLog
    @GetMapping("/getUserCollect")
    @ApiOperation("获取指定用户的收藏")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "用户id",required = true),
            @ApiImplicitParam(name = "pageNumber",value = "显示页码",required = true),
            @ApiImplicitParam(name = "pageSize",value = "每页条数",required = true)
    })
    public ResponseResult<?> getUserCollect(Long userId,Integer pageNumber,Integer pageSize){
        return userService.getUserCollect(userId,pageNumber,pageSize);
    }

    @AutoLog
    @GetMapping("/searchUser")
    @ApiOperation("模糊查询用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "content",value = "搜索框内容"),
            @ApiImplicitParam(name = "pageNumber",value = "显示页码",required = true),
            @ApiImplicitParam(name = "pageSize",value = "每页条数",required = true)
    })
    public ResponseResult<?> searchUser(String content,Integer pageNumber,Integer pageSize){
        return userService.searchUser(content,pageNumber,pageSize);
    }
}
