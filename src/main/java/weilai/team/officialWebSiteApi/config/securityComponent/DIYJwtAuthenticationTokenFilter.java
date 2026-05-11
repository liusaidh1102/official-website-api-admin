package weilai.team.officialWebSiteApi.config.securityComponent;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import weilai.team.officialWebSiteApi.entity.admin.DO.User;
import weilai.team.officialWebSiteApi.util.*;
import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Component
public class DIYJwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Resource
    private RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("JwtFilter 执行 一次");
        String token = request.getHeader(Values.TOKEN_PARAM_NAME);

        // 1. 无 token → 直接放行，交给后面 Spring Security 处理
        if (MyString.isNull(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        String[] tokenSplit = token.split(" ");
        // 2. token 格式错误 → 直接返回前端，不抛异常
        if (tokenSplit.length != 2 || !Values.TOKEN_PREFIX.equals(tokenSplit[0])) {
            responseError(response, ResponseResult.Unauthorized);
            return;
        }

        Map<String, Object> infoMap;
        try {
            infoMap = JWTUtil.getJtiAndSubject(tokenSplit[1]);
        } catch (ExpiredJwtException e) {
            // 3. token 过期 → 直接返回
            responseError(response, ResponseResult.LOGIN_FILE); // 你自己的过期枚举
            return;
        } catch (JwtException e) {
            // 4. token 无效 → 直接返回
            responseError(response, ResponseResult.Unauthorized);
            return;
        }

        String studentId = (String) infoMap.get("subject");
        String jti = (String) infoMap.get("jti");
        
        // 5. 检查token是否在黑名单中
        String blacklistKey = Values.TOKEN_BLACKLIST_PREFIX + jti;
        String blacklistValue = redisUtil.getRedisString(blacklistKey);
        if("blacklisted".equals(blacklistValue)){
            responseError(response, ResponseResult.Unauthorized);
            return;
        }
        
        String redisJti = redisUtil.getRedisString(Values.REDIS_TOKEN_ID + studentId);

        // 6. 账号在别处登录 → 直接返回（关键！）
        if (!jti.equals(redisJti)) {
            responseError(response, ResponseResult.LOGIN_FAIL_NOT_ONE);
            return;
        }

        String redisKey = Values.REDIS_TOKEN_PREFIX + studentId;
        User user = redisUtil.getRedisObject(redisKey, User.class);

        // 7. 用户信息不存在
        if (Objects.isNull(user)) {
            responseError(response, ResponseResult.LOGIN_FILE);
            return;
        }

        // 认证成功，存入上下文
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();
        emptyContext.setAuthentication(authenticationToken);
        SecurityContextHolder.setContext(emptyContext);

        // 放行
        filterChain.doFilter(request, response);
    }

    // 工具方法：直接返回 JSON 给前端
    private void responseError(HttpServletResponse response, ResponseResult result) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(result.getCode());
        new ObjectMapper().writeValue(response.getWriter(), result);
    }
}
