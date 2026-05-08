package weilai.team.officialWebSiteApi.service.admin;

import org.springframework.web.multipart.MultipartFile;
import weilai.team.officialWebSiteApi.entity.admin.DTO.*;
import weilai.team.officialWebSiteApi.util.ResponseResult;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * ClassName:UserManagerService
 * Description:
 *
 * @Author:独酌
 * @Create:2024/12/2 9:50
 */
public interface UserManagerService {

    /**
     * 获取团队用户列表
     * @return 响应结果
     */
    ResponseResult<?> getTeamUserList(Integer pageNumber, Integer pageSize);

    /**
     * 修改团队用户的信息
     * @param updateTeamUserInfoDTO 修改的信息
     * @return 响应结果
     */
    ResponseResult<?> modifyTeamUserInfo(UpdateTeamUserInfoDTO updateTeamUserInfoDTO);

    /**
     * 删除团队用户的信息
     * @param id 用户id
     * @return 响应结果
     */
    ResponseResult<?> deleteTeamUserInfo(List<Long> id);

    /**
     * 获取用户列表
     * @param grade 年级
     * @param group 组
     * @param pageNumber 页码
     * @param pageSize 页面大小
     * @return 响应结果
     */
    ResponseResult<?> getUserListByGroup(String grade, String group, Integer pageNumber, Integer pageSize);

    /**
     * 修改用户的权限
     *
     * @param modifyUserAuthDTO 修改的信息
     * @param request 登录用户的信息
     * @return 响应结果
     */
    ResponseResult<?> modifyUserAuthorities(ModifyUserAuthDTO modifyUserAuthDTO, HttpServletRequest request);

    /**
     * 模糊查询用户，根据权限和搜索框内容（根据姓名查询）
     *
     * @param content     搜索框内容
     * @param authorities 权限
     * @param pageNumber  页码
     * @param pageSize    页面大小
     * @return 响应结果
     */
    ResponseResult<?> reachUser(String content, String[] authorities, Integer pageNumber, Integer pageSize);

    /**
     * 搜索通讯录的用户
     * @param content 搜索框内容
     * @return 响应结果
     */
    ResponseResult<?> searchTeamUser(String content);

    /**
     * 根据用户的id获取团队用户
     * @param userId 用户id
     * @return 响应结果
     */
    ResponseResult<?> getTeamUser(Long userId);

    /**
     * 设置组长
     * @param setUserGroupLadle 设置的信息
     * @return 响应结果
     */
    ResponseResult<?> setGroupLabel(SetUserGroupLadle setUserGroupLadle);

    /**
     * 根据excel表批量导入用户
     * @param file excel表
     * @return 响应结果
     */
    ResponseResult<?> addUsers(MultipartFile file);

    /**
     * 添加单独的用户
     * @param addUserDTO 添加的用户
     * @return 响应结果
     */
    ResponseResult<?> addUser(AddUserDTO addUserDTO);

    /**
     * 根据年级获取用户
     * @param grade 年级
     * @return 响应结果
     */
    ResponseResult<?> getUsersByGrade(String grade);

    /**
     * 修改多个用户的信息
     * @param modifyManyUserDTO 修改的信息
     * @return 响应结果
     */
    ResponseResult<?> modifyManyUser(ModifyManyUserDTO modifyManyUserDTO);

    /**
     * 取消组长
     *
     * @param userId 用户id
     * @param group  组
     * @return 响应结果
     */
    ResponseResult<?> cancelGroupLarder(Long userId, String group);
}
