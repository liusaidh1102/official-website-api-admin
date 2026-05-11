package weilai.team.officialWebSiteApi.controller.admin;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import weilai.team.officialWebSiteApi.entity.admin.DTO.AccountPasswordDTO;
import weilai.team.officialWebSiteApi.entity.admin.DTO.FindPasswordDTO;
import weilai.team.officialWebSiteApi.service.admin.LoginService;
import weilai.team.officialWebSiteApi.service.admin.annotation.AutoLog;
import weilai.team.officialWebSiteApi.service.admin.annotation.CurrentLimiting;
import weilai.team.officialWebSiteApi.util.ResponseResult;
import weilai.team.officialWebSiteApi.util.Values;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@Api(tags = "登录页面相关操作",description = " ")
@RequestMapping("/index")
@Slf4j
public class LoginController {

    @Resource
    private LoginService loginService;

    @AutoLog
    @CurrentLimiting
    @ApiOperation("登录")
    @PostMapping("/login")
    public synchronized ResponseResult<?> login(@Valid @RequestBody AccountPasswordDTO accountPasswordDTO, HttpServletResponse response){
        return loginService.login(accountPasswordDTO,response);
    }

    @AutoLog
    @ApiOperation("获取验证码")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "email",value = "邮箱",dataType = "String",paramType = "path")
    })
    @GetMapping("/sendEmailCode/{email}")
    public synchronized ResponseResult<?> sendEmailCode(@PathVariable String email){
        log.info("获取验证码");
        return loginService.sendEmailCode(email);
    }

    @AutoLog
    @ApiOperation("找回密码")
    @PutMapping("/findPassword")
    public synchronized ResponseResult<?> findPassword(@Valid @RequestBody FindPasswordDTO findPasswordDTO){
        return loginService.findPassword(findPasswordDTO);
    }

    @AutoLog
    @ApiOperation("注销登录")
    @DeleteMapping("/logout")
    public ResponseResult<?> logout(HttpServletRequest request, HttpServletResponse response){
        return loginService.logout(request, response);
    }

    @AutoLog
    @CurrentLimiting
    @ApiOperation("刷新token")
    @PostMapping("/refreshToken")
    public ResponseResult<?> refreshToken(HttpServletRequest request, HttpServletResponse response){
        // 从Cookie中获取refreshToken
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (javax.servlet.http.Cookie cookie : request.getCookies()) {
                if ("refresh_token".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        
        if (org.apache.commons.lang3.StringUtils.isBlank(refreshToken)) {
            return ResponseResult.Unauthorized;
        }
        
        return loginService.refreshToken(refreshToken, response);
    }


    @AutoLog
    @ApiOperation("获取概况信息")
    @GetMapping("/getSummarize")
    public ResponseResult<?> getSummarize(){
        return loginService.getSummarize();
    }


}
