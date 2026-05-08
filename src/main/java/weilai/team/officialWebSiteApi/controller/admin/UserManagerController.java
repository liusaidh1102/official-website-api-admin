package weilai.team.officialWebSiteApi.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Delete;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import weilai.team.officialWebSiteApi.entity.admin.DTO.*;
import weilai.team.officialWebSiteApi.service.admin.UserManagerService;
import weilai.team.officialWebSiteApi.service.admin.annotation.AutoLog;
import weilai.team.officialWebSiteApi.util.ResponseResult;
import weilai.team.officialWebSiteApi.util.UserUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;

/**
 * ClassName:UserManagerController
 * Description:
 *
 * @Author:独酌
 * @Create:2024/12/2 9:50
 */
@RestController
@RequestMapping("/userManager")
@Api(tags = "用户管理相关操作",description = " ")
public class UserManagerController {

    @Resource
    private UserManagerService userManagerService;

    /*
    权限管理
     */
    @AutoLog
    @PutMapping("/permission/resetUserAuthorities")
    @ApiOperation("修改用户的权限")
    public synchronized ResponseResult<?> modifyUserAuthorities(@Valid @RequestBody ModifyUserAuthDTO modifyUserAuthDTO, HttpServletRequest request) {
        return userManagerService.modifyUserAuthorities(modifyUserAuthDTO,request);
    }

    @AutoLog
    @GetMapping("/permission/searchUser")
    @ApiOperation("模糊查询用户，空白查询所有用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "content", value = "搜索内容"),
            @ApiImplicitParam(name = "authorities", value = "权限"),
            @ApiImplicitParam(name = "pageNumber", value = "页码", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", required = true)
    })
    public ResponseResult<?> reachUser(String content, String[] authorities, Integer pageNumber, Integer pageSize) {
        return userManagerService.reachUser(content, authorities, pageNumber, pageSize);
    }

    /*
    通讯录
     */
    @AutoLog
    @GetMapping("/teamInfo/getTeamUserList/{pageNumber}/{pageSize}")
    @ApiOperation("获取团队用户的列表")
    public ResponseResult<?> getTeamUserList(@PathVariable Integer pageNumber, @PathVariable Integer pageSize) {
        return userManagerService.getTeamUserList(pageNumber, pageSize);
    }

    @AutoLog
    @GetMapping("/teamInfo/getUserListByGroup")
    @ApiOperation("根据年级和组别获取用户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "grade", value = "年级", required = true),
            @ApiImplicitParam(name = "group", value = "组别", required = true),
            @ApiImplicitParam(name = "pageNumber", value = "页码", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", required = true)
    })
    public ResponseResult<?> getUserListByGroup(String grade, String group, Integer pageNumber, Integer pageSize) {
        return userManagerService.getUserListByGroup(grade, group, pageNumber, pageSize);
    }

    @AutoLog
    @PutMapping("/teamInfo/modifyTeamUserInfo")
    @ApiOperation("修改团队用户的信息")
    public synchronized ResponseResult<?> modifyTeamUserInfo(@RequestBody UpdateTeamUserInfoDTO updateTeamUserInfoDTO) {
        return userManagerService.modifyTeamUserInfo(updateTeamUserInfoDTO);
    }

    @AutoLog
    @DeleteMapping("/teamInfo/deleteTeamUserInfo")
    @ApiOperation("删除团队用户的信息")
    public synchronized ResponseResult<?> deleteTeamUserInfo(Long[] id) {
        return userManagerService.deleteTeamUserInfo(Arrays.asList(id));
    }

    @AutoLog
    @GetMapping("/teamInfo/searchTeamUser")
    @ApiOperation("模糊查询团队用户")
    @ApiImplicitParam(name = "content", value = "搜索内容", required = true)
    public ResponseResult<?> searchTeamUser(String content) {
        return userManagerService.searchTeamUser(content);
    }

    @AutoLog
    @GetMapping("/teamInfo/getTeamUser")
    @ApiOperation("获取团队用户")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true)
    public ResponseResult<?> getTeamUser(Long userId) {
        return userManagerService.getTeamUser(userId);
    }

    @AutoLog
    @PostMapping("/teamInfo/setGroupLabel")
    @ApiOperation("设置组长")
    public ResponseResult<?> setGroupLabel(@Valid @RequestBody SetUserGroupLadle setUserGroupLadle) {
        return userManagerService.setGroupLabel(setUserGroupLadle);
    }

    @AutoLog
    @PostMapping("/teamInfo/addUsers")
    @ApiOperation("批量导入用户")
    public ResponseResult<?> addUsers(MultipartFile file){
        return userManagerService.addUsers(file);
    }

    @AutoLog
    @PostMapping("/teamInfo/assUser")
    @ApiOperation("添加用户")
    public ResponseResult<?> addUser(@Valid @RequestBody AddUserDTO addUserDTO){
        return userManagerService.addUser(addUserDTO);
    }

    @AutoLog
    @GetMapping("/teamInfo/getUsersByGrade")
    @ApiOperation("根据年级获取用户")
    @ApiImplicitParam(name = "grade", value = "年级", required = true)
    public ResponseResult<?> getUsersByGrade(String grade){
        return userManagerService.getUsersByGrade(grade);
    }

    @AutoLog
    @PutMapping("/teamInfo/modifyManyUser")
    @ApiOperation("修改多个用户")
    public ResponseResult<?> modifyManyUser(@Valid @RequestBody ModifyManyUserDTO modifyManyUserDTO){
        return userManagerService.modifyManyUser(modifyManyUserDTO);
    }

    @AutoLog
    @DeleteMapping("/teamInfo/cancelGroupLarder/{userId}/{group}")
    @ApiOperation("取消组长")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true),
            @ApiImplicitParam(name = "group", value = "组别(2023$1)",example = "2023$1",required = true)
    })
    public ResponseResult<?> cancelGroupLarder(@PathVariable Long userId, @PathVariable String group){
        return userManagerService.cancelGroupLarder(userId,group);
    }
}
