package weilai.team.officialWebSiteApi.config.securityComponent;

import org.springframework.security.authentication.InsufficientAuthenticationException;
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

/**
 * 访问受保护资源时，如果用户没有认证，会到这里处理请求
 *
 *
 *
 *
 * 客户端请求
 *     ↓
 * DIYJwtAuthenticationTokenFilter (JWT过滤器)
 *     ↓
 * [发生异常] → 抛出 AccountAlreadyLoginException
 *     ↓
 * Spring Security FilterChainProxy
 *     ↓
 * ExceptionTranslationFilter (异常翻译过滤器，只捕获两类异常  认证异常AuthenticationException 和 权限异常AccessDeniedException)
 *     ↓
 * DIYAuthenticationEntryPoint (认证入口处理器)
 *     ↓
 * 返回 JSON 响应给客户端
 */
@Component
public class DIYAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException e) throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        ResponseResult<?> result;


        if (e instanceof InsufficientAuthenticationException) {
            result = ResponseResult.Unauthorized;
        } else {
            result = ResponseResult.LOGIN_FILE;
        }

        response.setStatus(result.getCode());
        WebUtil.sendResponse(response, result, "application/json;charset=UTF-8");
    }
}