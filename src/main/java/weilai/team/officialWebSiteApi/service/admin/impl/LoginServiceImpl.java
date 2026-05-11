package weilai.team.officialWebSiteApi.service.admin.impl;

import com.itextpdf.styledxmlparser.jsoup.parser.ParseError;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import weilai.team.officialWebSiteApi.entity.admin.DO.User;
import weilai.team.officialWebSiteApi.entity.admin.DTO.AccountPasswordDTO;
import weilai.team.officialWebSiteApi.entity.admin.DTO.FindPasswordDTO;
import weilai.team.officialWebSiteApi.entity.admin.VO.SummarizeVO;
import weilai.team.officialWebSiteApi.entity.admin.VO.TokenVO;
import weilai.team.officialWebSiteApi.mapper.admin.UserMapper;
import weilai.team.officialWebSiteApi.service.admin.LoginService;
import org.springframework.stereotype.Service;
import weilai.team.officialWebSiteApi.util.*;
import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
* @author 王科林
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2024-11-09 22:11:08
*/
@Service
public class LoginServiceImpl implements LoginService {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private UserMapper userMapper;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private JavaMailSender javaMailSender;

    @Resource
    private UserUtil userUtil;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private RedisCacheUtil redisCacheUtil;

    @Override
    public ResponseResult<?> login(AccountPasswordDTO accountPasswordDTO, HttpServletResponse response) {
        //获取用户信息
        User tempUser = userMapper.selectAllByUsernameOrEmail(accountPasswordDTO.getAccount());
        if(tempUser == null){
            return ResponseResult.LOGIN_FAIL_USER_NOT_EXIST;
        }

        //创建认证对象
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(accountPasswordDTO.getAccount(),accountPasswordDTO.getPassword());
        //进行认证，底层交给DIYUserDetailsService.loadUserByUsername()方法来加载用户信息，并进行密码比对
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        if(Objects.isNull(authenticate)){
            return ResponseResult.LOGIN_FILE;
        }

        //防护，判断是否存在有效token，如果不存在，就继续发送token，
        //如果存在，说明已经登录过了，该账号已经储存在redis里面了，因此，删除有效的token，重新登录
//        User t = redisUtil.getRedisObject(Values.REDIS_TOKEN_PREFIX + tempUser.getUsername(), User.class);
//        if(t != null){
//            boolean a = redisUtil.deleteRedis(Values.REDIS_TOKEN_ID + tempUser.getUsername());
//            boolean b = redisUtil.deleteRedis(Values.REDIS_TOKEN_PREFIX + tempUser.getUsername());
//            return a || b ? ResponseResult.LOGIN_FAIL_NOT_ONE : ResponseResult.SERVICE_ERROR;
//        }

        //认证通过，获取用户信息
        User user = (User) authenticate.getPrincipal();


        //向redis中存入用户的登录时间
        Date currentLoginTime = user.getCurrentLoginTime();
        user.setLastLoginTime(currentLoginTime);
        user.setCurrentLoginTime(new Date());

        //销毁密码
        user.setPassword(null);

        //更新数据库中的登录时间
        int i = userMapper.updateLastLoginTimeByUsernameOrEmail(user.getLastLoginTime(),user.getCurrentLoginTime(),accountPasswordDTO.getAccount());
        if(i <= 0){
            return ResponseResult.SERVICE_ERROR;
        }


        //通过 username 生成 token 并返回给前端
        String username = user.getUsername();
        Map<String, String> accessToken = JWTUtil.createToken(username,Values.OUT_TIME);
        Map<String, String> refreshToken = JWTUtil.createToken(username,Values.REFRESH_TOKEN_OUT_TIME);


        //存入token的唯一表示
        boolean access = redisUtil.setRedisStringWithOutTime(Values.REDIS_TOKEN_ID + username, accessToken.get("jti"), Values.OUT_TIME);
        boolean refresh = redisUtil.setRedisStringWithOutTime(Values.REDIS_TOKEN_REFRESH + username, refreshToken.get("jti"), Values.REFRESH_TOKEN_OUT_TIME);

        //将用户信息存入redis中，以 用户id 为键，用户信息对象的json为值
        boolean b = redisUtil.setRedisObjectWithOutTime(Values.REDIS_TOKEN_PREFIX + username, user, Values.OUT_TIME);

        // 2. 构造 Cookie
        Cookie cookie = new Cookie("refresh_token", refreshToken.get("token"));
        cookie.setHttpOnly(true);   // 核心：JS不可读
        cookie.setSecure(true);     // HTTPS 才发送
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7天

        response.addCookie(cookie);

        //封装返回数据信息
        TokenVO tokenVO = new TokenVO(accessToken.get("token"), user.getId(), user.getAuth());

        return access && b && refresh ? ResponseResult.LOGIN_SUCCESS.put(tokenVO) : ResponseResult.SERVICE_ERROR;
    }

    @Override
    public ResponseResult<?> sendEmailCode(String email) {
        //判断邮箱格式
        if(email == null || !email.matches(Values.EMAIL_FORMAT)){
            return ResponseResult.EMAIL_FORMAT_ERROR;
        }

        //判断用户是否存在
        User tempUser = userMapper.selectAllByUsernameOrEmail(email);
        if(tempUser == null){
            return ResponseResult.LOGIN_FAIL_USER_NOT_EXIST;
        }

        //判断验证码是否过期
        String isExistenceCode = redisUtil.getRedisString(Values.REDIS_CAPTCHA_PREFIX + email);
        if(isExistenceCode != null){
            return ResponseResult.EMAIL_CAPTCHA_FOUND;
        }


        //发送并获取验证码
        String code = EmailUtil.getCode(email,javaMailSender);

        //存入redis中，以 email为键，验证码为值，设置验证码为 5 分钟
        redisUtil.setRedisStringWithOutTime(Values.REDIS_CAPTCHA_PREFIX + email,code,1000 * 60 * 5);

        return ResponseResult.EMAIL_CAPTCHA_SEND_SUCCESS;
    }

    @Override
    public ResponseResult<?> findPassword(FindPasswordDTO findPasswordDTO) {
        String email = findPasswordDTO.getEmail();
        String code = findPasswordDTO.getCode();
        String newPassword = findPasswordDTO.getNewPassword();

        //判断邮箱格式
        if(!email.matches(Values.EMAIL_FORMAT)){
            return ResponseResult.EMAIL_FORMAT_ERROR;
        }

        //验证码是否超时
        String realCode = redisUtil.getRedisString(Values.REDIS_CAPTCHA_PREFIX + email);
        if(realCode == null){
            return ResponseResult.EMAIL_CAPTCHA_NOT_FOUND;
        }

        //判断验证码是否正确
        if(!realCode.equals(code)){
            return ResponseResult.EMAIL_CAPTCHA_FILE;
        }

        int i = userMapper.updatePasswordByEmail(passwordEncoder.encode(newPassword), email);

        return i >= 1 ? ResponseResult.UPDATE_PASSWORD_SUCCESS : ResponseResult.LOGIN_FAIL_USER_NOT_EXIST;
    }

    @Override
    public ResponseResult<?> logout(HttpServletRequest request, HttpServletResponse response) {
        // 获取用户信息和token
        User userInfo = userUtil.getUserInfo(request);
        String token = request.getHeader(Values.TOKEN_PARAM_NAME);
        
        if(userInfo != null && StringUtils.isNotBlank(token)){
            // 提取token字符串（去掉Bearer前缀）
            String[] tokenSplit = token.split(" ");
            if(tokenSplit.length == 2 && Values.TOKEN_PREFIX.equals(tokenSplit[0])){
                String jwtToken = tokenSplit[1];
                
                try {
                    // 获取token的剩余过期时间
                    Map<String, Object> infoMap = JWTUtil.getJtiAndSubject(jwtToken);
                    String jti = (String) infoMap.get("jti");
                    
                    // 将token加入黑名单，设置过期时间为token剩余有效期
                    // 使用jti作为黑名单的key，确保唯一性
                    redisUtil.setRedisStringWithOutTime(
                        Values.TOKEN_BLACKLIST_PREFIX + jti, 
                        "blacklisted", 
                        Values.OUT_TIME
                    );
                    
                    LogUtil.info("用户退出登录，token已加入黑名单: username=" + userInfo.getUsername() + ", jti=" + jti);
                } catch (Exception e) {
                    LogUtil.Error("退出登录时处理token失败", e);
                }
            }
            
            // 删除Redis中的用户会话信息
            redisUtil.deleteRedis(Values.REDIS_TOKEN_ID + userInfo.getUsername());
            redisUtil.deleteRedis(Values.REDIS_TOKEN_PREFIX + userInfo.getUsername());
            redisUtil.deleteRedis(Values.USER_INFO_PREFIX + userInfo.getId());
            redisUtil.deleteRedis(Values.REDIS_TOKEN_REFRESH + userInfo.getUsername());
        }
        
        // 清除refresh_token Cookie
        Cookie cookie = new Cookie("refresh_token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 立即过期
        response.addCookie(cookie);
        
        return ResponseResult.OK;
    }

    @Override
    public ResponseResult<?> refreshToken(String refreshToken, HttpServletResponse response) {
        // 1. 验证refreshToken是否为空
        if (StringUtils.isBlank(refreshToken)) {
            return ResponseResult.Unauthorized;
        }
        
        try {
            // 2. 解析refreshToken获取用户信息
            Map<String, Object> infoMap = JWTUtil.getJtiAndSubject(refreshToken);
            String username = (String) infoMap.get("subject");
            String refreshJti = (String) infoMap.get("jti");
            
            // 3. 验证refreshToken是否在Redis中存在且匹配
            String storedRefreshJti = redisUtil.getRedisString(Values.REDIS_TOKEN_REFRESH + username);
            if (storedRefreshJti == null || !storedRefreshJti.equals(refreshJti)) {
                return ResponseResult.LOGIN_FILE;
            }
            
            // 4. 从Redis中获取用户信息
            User user = redisUtil.getRedisObject(Values.REDIS_TOKEN_PREFIX + username, User.class);
            if (user == null) {
                // 如果用户信息不存在，从数据库查询
                user = userMapper.selectAllByUsernameOrEmail(username);
                if (user == null) {
                    return ResponseResult.LOGIN_FAIL_USER_NOT_EXIST;
                }
                // 销毁密码
                user.setPassword(null);
            }
            
            // 5. 生成新的accessToken
            Map<String, String> newAccessToken = JWTUtil.createToken(username, Values.OUT_TIME);
            
            // 6. 生成新的refreshToken（刷新后轮换）
            Map<String, String> newRefreshToken = JWTUtil.createToken(username, Values.REFRESH_TOKEN_OUT_TIME);
            
            // 7. 更新Redis中的accessToken jti
            boolean accessSuccess = redisUtil.setRedisStringWithOutTime(
                Values.REDIS_TOKEN_ID + username, 
                newAccessToken.get("jti"), 
                Values.OUT_TIME
            );
            
            // 8. 更新Redis中的refreshToken jti
            boolean refreshSuccess = redisUtil.setRedisStringWithOutTime(
                Values.REDIS_TOKEN_REFRESH + username,
                newRefreshToken.get("jti"),
                Values.REFRESH_TOKEN_OUT_TIME
            );
            
            if (!accessSuccess || !refreshSuccess) {
                return ResponseResult.SERVICE_ERROR;
            }
            
            // 9. 更新用户信息缓存
            redisUtil.setRedisObjectWithOutTime(Values.REDIS_TOKEN_PREFIX + username, user, Values.OUT_TIME);
            
            // 10. 将新的refreshToken设置到Cookie中
            Cookie cookie = new Cookie("refresh_token", newRefreshToken.get("token"));
            cookie.setHttpOnly(true);   // JS不可读
            cookie.setSecure(true);     // HTTPS才发送
            cookie.setPath("/");
            cookie.setMaxAge((int) (Values.REFRESH_TOKEN_OUT_TIME / 1000)); // 7天
            response.addCookie(cookie);
            
            // 11. 封装返回数据（只返回accessToken）
            TokenVO tokenVO = new TokenVO(
                newAccessToken.get("token"), 
                user.getId(), 
                user.getAuth()
            );
            
            LogUtil.info("用户刷新token成功: username=" + username);
            return ResponseResult.OK.put(tokenVO);
            
        } catch (Exception e) {
            LogUtil.Error("刷新token失败", e);
            return ResponseResult.LOGIN_FILE;
        }
    }



    @Override
    public ResponseResult<?> getSummarize() {
        String keyPrefix = "Summarize";
        SummarizeVO summarizeVO = redisCacheUtil.queryWithPassThroughByString(
                keyPrefix,
                "",
                SummarizeVO.class,
                () -> {
                    SummarizeVO temp = userMapper.selectSummarize();
                    List<SummarizeVO.Area> areas = userMapper.selectSummarizeArea();
                    List<SummarizeVO.Per> perByGrade = userMapper.selectSummarizePer();
                    // 出去null值
                    perByGrade.removeIf(Objects::isNull);
                    perByGrade.sort(Comparator.comparingInt(SummarizeVO.Per::getGrade));
                    temp.setAreas(areas);
                    temp.setPerByGrade(perByGrade);
                    return temp;
                },
                2L,
                TimeUnit.MINUTES
        );
        return ResponseResult.OK.put(summarizeVO);
    }
}




