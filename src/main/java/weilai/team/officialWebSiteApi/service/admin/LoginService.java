package weilai.team.officialWebSiteApi.service.admin;

import weilai.team.officialWebSiteApi.entity.admin.DTO.AccountPasswordDTO;
import weilai.team.officialWebSiteApi.entity.admin.DTO.FindPasswordDTO;
import weilai.team.officialWebSiteApi.entity.admin.VO.TokenVO;
import weilai.team.officialWebSiteApi.util.ResponseResult;

import javax.servlet.http.HttpServletRequest;

/**
* @author 王科林
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2024-11-09 22:11:08
*/
public interface LoginService {

    /**
     * 用户名密码登录
     * @param accountPasswordDTO 用户名密码
     * @return 登录结果
     */
    ResponseResult<?> login(AccountPasswordDTO accountPasswordDTO);

    /**
     * 发送邮箱验证码
     * @param email 邮箱
     * @return 发送结果
     */
    ResponseResult<?> sendEmailCode(String email);

    /**
     * 找回密码
     * @param findPasswordDTO 找回密码验证信息
     * @return 找回结果
     */
    ResponseResult<?> findPassword(FindPasswordDTO findPasswordDTO);

    /**
     * 退出登录
     * @param request 当前用户的信息
     * @return 退出结果
     */
    ResponseResult<?> logout(HttpServletRequest request);

    /**
     * 获取概况信息
     * @return 概况信息
     */
    ResponseResult<?> getSummarize();

}
