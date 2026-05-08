package weilai.team.officialWebSiteApi.config.securityComponent;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import weilai.team.officialWebSiteApi.util.ResponseResult;
import weilai.team.officialWebSiteApi.util.WebUtil;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component
public class DIYAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        ResponseResult<?> result;
        if(Objects.equals(e.getMessage(), "Full authentication is required to access this resource")){
            //没有token、token过期
            result = ResponseResult.Unauthorized;
        } else {
            //密码、用户名错误
            result = ResponseResult.LOGIN_FILE;
        }
        WebUtil.sendResponse(httpServletResponse,result,"application/json;charset=UTF-8");
    }
}
