package weilai.team.officialWebSiteApi.config.securityComponent;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class DIYJwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Resource
    private RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //获取token
        String token = request.getHeader(Values.TOKEN_PARAM_NAME);

        //判断是否携带token
        if(MyString.isNull(token)){
            //如果token不存在，直接放行
            filterChain.doFilter(request, response);
            return;
        }

        String[] tokenSplit = token.split(" ");
        if(tokenSplit.length != 2 || !Values.TOKEN_PREFIX.equals(tokenSplit[0])){
            //如果token不合法，直接放行
            filterChain.doFilter(request, response);
            return;
        }

        String info = null;
        try{
            //解析token
            info = JWTUtil.getInformation(tokenSplit[1]);
        } catch (ExpiredJwtException e){
            //如果token超时，就直接放行
            filterChain.doFilter(request, response);
            return;
        }

        //根据解析的内容，继续解析分离出信息
        String[] split = info.split("\\$");
        if(split.length != 2) {
            //如果信息错误，直接放行
            filterChain.doFilter(request, response);
            return;
        }
        String username = split[0];
        String currentTokenId = split[1];

        //从redis中获取token的id
        String tokenId = redisUtil.getRedisString(Values.REDIS_TOKEN_ID + username);

        //判断tokenId是否存在相同
        if(!currentTokenId.equals(tokenId)){
            //如果tokenId不相同，说明已将有账号登录过了，不予许访问
            filterChain.doFilter(request, response);
            return;
        }

        //从redis中获取用户信息
        String redisKey = Values.REDIS_TOKEN_PREFIX + username;
        User user = redisUtil.getRedisObject(redisKey, User.class);

        //判断用户是否存在
        if(Objects.isNull(user)){
            //如果用户不存在，不予许访问，未认证
            filterChain.doFilter(request, response);
            return;
        }

        //如果是已经认证的状态，调用三个参数的构造器，就把用户的详细信息放到第一个参数，第二个参数为 null
        //如果是第一次登陆（未认证状态），调用两个参数的构造器，就将用户的账号放到第一个参数，密码放到第二个参数
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities());

        //存入SecurityContextHolder中，同一个请求的 SecurityContextHolder 相同，
        // 不同的请求的 SecurityContextHolder 不相同
        //因此，退出登录时，不用删除 SecurityContextHolder 中的值
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        //重新定义用户凭据的过期时间，保证用户在登录过程中，不会出现token过期的现象
        redisUtil.reSetOutTime(Values.REDIS_TOKEN_ID + username,Values.OUT_TIME, TimeUnit.MILLISECONDS);
        redisUtil.reSetOutTime(redisKey,Values.OUT_TIME, TimeUnit.MILLISECONDS);

        //放行
        filterChain.doFilter(request, response);
    }
}
