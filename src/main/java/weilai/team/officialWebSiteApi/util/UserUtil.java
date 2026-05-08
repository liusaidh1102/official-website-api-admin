package weilai.team.officialWebSiteApi.util;

import org.springframework.stereotype.Component;
import weilai.team.officialWebSiteApi.entity.admin.DO.User;
import weilai.team.officialWebSiteApi.mapper.admin.UserMapper;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * ClassName:UserUtil
 * Description:
 *
 * @Author:独酌
 * @Create:2024/11/12 20:15
 */
@Component
public class UserUtil {
    @Resource
    RedisUtil redisUtil;

    /**
     * 获取当前登录的用户信息
     * @param request 请求头
     * @return 用户信息
     */
    public User getUserInfo(HttpServletRequest request){
        String token = request.getHeader(Values.TOKEN_PARAM_NAME);

        if(token == null) return null;

        String[] tokenSplit = token.split(" ");
        if(tokenSplit.length != 2 || !Values.TOKEN_PREFIX.equals(tokenSplit[0])){
            //如果token不合法，直接放行
            return null;
        }

        String username = JWTUtil.getInformation(token.split(" ")[1]).split("\\$")[0];
        return redisUtil.getRedisObject(Values.REDIS_TOKEN_PREFIX + username, User.class);
    }
}
