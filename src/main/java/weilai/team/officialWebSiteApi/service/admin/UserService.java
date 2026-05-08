package weilai.team.officialWebSiteApi.service.admin;

import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
import org.springframework.web.multipart.MultipartFile;
import weilai.team.officialWebSiteApi.entity.admin.DTO.AutomaticUserCourseDTO;
import weilai.team.officialWebSiteApi.entity.admin.DTO.UpdateUserInfoDTO;
import weilai.team.officialWebSiteApi.entity.admin.DTO.UserCourseDTO;
import weilai.team.officialWebSiteApi.util.ResponseResult;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * ClassName:UserService
 * Description:
 *
 * @Author:独酌
 * @Create:2024/11/12 11:45
 */
public interface UserService {

    /**
     * 账号获取用户信息
     * @param userId 用户账号[学号]
     * @return 用户信息
     */
    ResponseResult<?> getUserInfoByUserId(Long userId);

    /**
     * 获取用户的生活照片
     * @param userId 用户id
     * @return 用户生活照片
     */
    ResponseResult<?> getUserLifePhotoByUserId(Long userId);

    /**
     * 跟新当前用户的信息
     *
     * @param updateUserInfoDTO 更新后的数据
     * @param request 当前登录用户
     * @return 更新结果
     */
    ResponseResult<?> updateUserInfo(UpdateUserInfoDTO updateUserInfoDTO, HttpServletRequest request);

    /**
     * 获取用户的课程信息
     * @param userId 用户id
     * @return 用户课程信息
     */
    ResponseResult<?> getUserCourse(Long userId);

    /**
     * 获取用户的帖子信息
     *
     * @param userId   用户id
     * @param pageNum 当前页码
     * @param pageSize  每页显示数量
     * @return 用户帖子信息
     */
    ResponseResult<?> getUserPost(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 根据用户id获取用户的收藏信息
     * @param userId 用户id
     * @param pageNumber 当前页码
     * @param pageSize  每页显示数量
     */
    ResponseResult<?> getUserCollect(Long userId, Integer pageNumber, Integer pageSize);

    /**
     * 添加用户的课程信息
     * @param userCourseDTO 用户课程信息
     * @param request 当前登录用户
     * @return 添加结果
     */
    ResponseResult<?> setUserCourse(UserCourseDTO userCourseDTO,Long userId,HttpServletRequest request);

    /**
     * 更新用户的课程信息
     *
     * @param oneCourseId 课程id
     * @param request 当前登录用户
     * @return 更新结果
     */
    ResponseResult<?> deleteUserCourse(Long oneCourseId, HttpServletRequest request);

    /**
     * 修改当前用户头像
     * @param headPortrait 头像文件
     * @param request 当前登录用户
     * @return 更新结果
     */
    ResponseResult<?> updateUserHeadPortrait(MultipartFile headPortrait, HttpServletRequest request);

    /**
     * 增加当前用户生活照片
     * @param lifePhoto 生活照片文件
     * @param request 当前登录用户
     * @return 更新结果
     */
    ResponseResult<?> addUserLifePhoto(List<MultipartFile> lifePhoto, HttpServletRequest request);

    /**
     * 删除当前用户生活照片
     * @param url 照片url
     * @param request 当前登录用户
     * @return 更新结果
     */
    ResponseResult<?> deleteUserLifePhoto(String url, HttpServletRequest request);

    /**
     * 根据课程id修改课程
     *
     * @param userCourseDTO 课程信息
     * @param request 当前登录用户
     * @return 更新结果
     */
    ResponseResult<?> updateUserCourse(UserCourseDTO userCourseDTO, HttpServletRequest request);

    /**
     * 搜索用户
     *
     * @param content 搜索框内容
     * @param pageNumber 当前页码
     * @param pageSize  每页显示数量
     * @return 搜索结果
     */
    ResponseResult<?> searchUser(String content, Integer pageNumber, Integer pageSize);


    /**
     * 每日自动更新当前用户的课表
     */
    int AutomaticUpdateUserCourse();

    /**
     * 添加课表
     */
    ResponseResult<?> addAutomaticUpdateUserCourse(AutomaticUserCourseDTO automaticUserCourseDTO, HttpServletRequest request);
}
