package weilai.team.officialWebSiteApi.service.admin.impl;

import com.itextpdf.styledxmlparser.jsoup.parser.ParseError;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
    public ResponseResult<?> login(AccountPasswordDTO accountPasswordDTO) {
        //获取用户信息
        User tempUser = userMapper.selectAllByUsernameOrEmail(accountPasswordDTO.getAccount());
        if(tempUser == null){
            return ResponseResult.LOGIN_FAIL_USER_NOT_EXIST;
        }

        //创建认证对象
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(accountPasswordDTO.getAccount(),accountPasswordDTO.getPassword());
        //进行认证
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        if(Objects.isNull(authenticate)){
            return ResponseResult.LOGIN_FILE;
        }

        //防护，判断是否存在有效token，如果不存在，就继续发送token，
        //如果存在，说明已经登录过了，该账号已经储存在redis里面了，因此，删除有效的token，重新登录
        User t = redisUtil.getRedisObject(Values.REDIS_TOKEN_PREFIX + tempUser.getUsername(), User.class);
        if(t != null){
            boolean a = redisUtil.deleteRedis(Values.REDIS_TOKEN_ID + tempUser.getUsername());
            boolean b = redisUtil.deleteRedis(Values.REDIS_TOKEN_PREFIX + tempUser.getUsername());
            return a || b ? ResponseResult.LOGIN_FAIL_NOT_ONE : ResponseResult.SERVICE_ERROR;
        }

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
        String tokenId = Long.toString(System.currentTimeMillis());
        String token = JWTUtil.createToken(username + "$" + tokenId);

        //存入token的唯一表示
        boolean a = redisUtil.setRedisStringWithOutTime(Values.REDIS_TOKEN_ID + username,tokenId,Values.OUT_TIME);
        //将用户信息存入redis中，以 token为键，用户信息对象的json为值
        boolean b = redisUtil.setRedisObjectWithOutTime(Values.REDIS_TOKEN_PREFIX + username, user, Values.OUT_TIME);


        //封装返回数据信息
        TokenVO tokenVO = new TokenVO(token,user.getId(),user.getAuth());

        return a && b ? ResponseResult.LOGIN_SUCCESS.put(tokenVO) : ResponseResult.SERVICE_ERROR;
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
    public ResponseResult<?> logout(HttpServletRequest request) {
        User userInfo = userUtil.getUserInfo(request);
        if(userInfo != null){
            boolean a = redisUtil.deleteRedis(Values.REDIS_TOKEN_ID + userInfo.getUsername());
            boolean b = redisUtil.deleteRedis(Values.REDIS_TOKEN_PREFIX + userInfo.getUsername());
            boolean c = redisUtil.deleteRedis(Values.USER_INFO_PREFIX + userInfo.getId());
            if(!b || !a || !c){
                return ResponseResult.SERVICE_ERROR;
            }
        }
        return ResponseResult.OK;
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




