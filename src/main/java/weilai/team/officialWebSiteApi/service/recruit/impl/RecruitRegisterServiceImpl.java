package weilai.team.officialWebSiteApi.service.recruit.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jacoco.agent.rt.internal_035b120.CoverageTransformer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import weilai.team.officialWebSiteApi.entity.recruit.DO.RecruitClass;
import weilai.team.officialWebSiteApi.entity.recruit.DO.RecruitUser;
import weilai.team.officialWebSiteApi.entity.recruit.DTO.RecruitFormDTO;
import weilai.team.officialWebSiteApi.entity.recruit.enums.FileTypeEnum;
import weilai.team.officialWebSiteApi.mapper.recruit.RecruitClassMapper;
import weilai.team.officialWebSiteApi.mapper.recruit.RecruitMapper;
import weilai.team.officialWebSiteApi.service.recruit.FileService;
import weilai.team.officialWebSiteApi.service.recruit.RecruitRegisterService;
import weilai.team.officialWebSiteApi.util.*;
import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.UUID;

import static weilai.team.officialWebSiteApi.entity.recruit.enums.FileDirEnum.RECRUIT;

/**
 * @author lzw
 * @date 2024/11/11 15:05
 * @description 作用：招新的报名相关业务实现
 */
@Slf4j
@Service
public class RecruitRegisterServiceImpl implements RecruitRegisterService {

    /*
         注入招新用户的mapper
     */
    @Resource
    private RecruitMapper recruitMapper;

    /*
         注入招新班级mapper
     */
    @Resource
    private RecruitClassMapper recruitClassMapper;


    /*
         注入redis的工具类
     */
    @Resource
    private RedisUtil redisUtil;

    /*
         注入邮件发送工具类
     */
    @Resource
    private JavaMailSender javaMailSender;
    @Autowired
    private MinioUtil minioUtil;

    @Autowired
    private ImageToPdfConverter converter;



    /**
     * 该service不能直接被其他的service调用，只能被controller调用，因为这里的参数校验是在controller中提前校验过了
     */
    @Override
    public ResponseResult<?> recruitRegister(RecruitFormDTO recruitFormDTO) {
        //1. 先进行基本参数格式的校验
        if (ObjectUtils.isEmpty(recruitFormDTO)) return ResponseResult.PARAM_IS_NOT_VALID;
        String sex = recruitFormDTO.getSex().trim();
        String email = recruitFormDTO.getEmail().trim();
        String studentId = recruitFormDTO.getStudentId().trim();
        String code = recruitFormDTO.getCode().trim();
        String qqNumber = recruitFormDTO.getQqNumber().trim();
        if (!sex.matches(Values.SEX_FORMAT)) return ResponseResult.SEX_IS_NOT_VALID;
        if (!studentId.matches(Values.STUDENT_ID_FORMAT)) return ResponseResult.STUDENT_ID_IS_NOT_VALID;
        if (!email.matches(Values.EMAIL_FORMAT)) return ResponseResult.EMAIL_FORMAT_ERROR;
        //判断qq号是不是5-10位数
        if (!qqNumber.matches(Values.QQ_NUMBER_FORMAT)) return ResponseResult.QQ_NUMBER_IS_NOT_VALID;


        //2. 文件的判断
        MultipartFile file1 = recruitFormDTO.getFile1();
        MultipartFile file2 = recruitFormDTO.getFile2();
        // 判断上传的文件是不是合法的
        boolean isValid1 = ImageUploadValidateUtil.validateImage(file1);
        boolean isValid2 =ImageUploadValidateUtil.validateImage(file2);
        if (!isValid1 || !isValid2){
            return ResponseResult.FILE_TYPE_ERROR;
        }

        //3. 判断验证码是否正确，获取redis里面的验证码
        String redisCode = redisUtil.getRedisString(Values.REDIS_RECRUIT_CAPTCHA_PREFIX + email);
        if (!StringUtils.equals(redisCode, code)) {
            //返回验证码错误的信息
            return ResponseResult.EMAIL_CAPTCHA_FILE;
        }

        log.info(email + "：验证码识别正确！");

        //进行对象的复制，将前端传过来的DTO复制为DO
        RecruitUser recruitUser = new RecruitUser();
        BeanUtils.copyProperties(recruitFormDTO, recruitUser);
        String lockKeyEmail = "recruit:register:lock:email:" + email;

        boolean locked = false;
        String lockValue = UUID.randomUUID().toString();
        try {
            String acquireLua = "return redis.call('set', KEYS[1], ARGV[1], 'NX', 'PX', ARGV[2]) and 1 or 0";
            Object acquired = redisUtil.executeLua(
                    acquireLua,
                    Collections.singletonList(lockKeyEmail),
                    Arrays.asList(lockValue, String.valueOf(TimeUnit.SECONDS.toMillis(60)))
            );
            locked = acquired instanceof Long && (Long) acquired == 1L;
            if (!locked) {
                return ResponseResult.REQUEST_LIMIT;
            }

            LambdaQueryWrapper<RecruitUser> wrapper = new QueryWrapper<RecruitUser>()
                    .lambda()
                    .eq(RecruitUser::getIsDeleted, 0)
                    .eq(RecruitUser::getEmail, email);

            if (recruitMapper.exists(wrapper)) {
                return ResponseResult.ALREADY_REGISTER;
            }

            log.info(email + "：没有报名过正确！");

            long time = System.currentTimeMillis();
            String url;
            try {
                ByteArrayOutputStream byteArrayOutputStream = converter.convertTwoImagesToPdf(file1.getInputStream(), file2.getInputStream());
                url = minioUtil.uploadFileByte(RECRUIT.getDirName(), studentId + "_" + time + ".pdf", byteArrayOutputStream);
            } catch (IOException e) {
                LogUtil.Error("文件上传失败", e);
                return ResponseResult.SERVICE_ERROR;
            }

            log.info(email + "：文件上传成功 " + url);
            recruitUser.setFileUrl(url);

            int rows = recruitMapper.insert(recruitUser);
            if (rows > 0) {
                log.info(email + "：插入数据库成功！");
                return ResponseResult.OK;
            }
            LogUtil.info("插入数据库失败");
            return ResponseResult.SERVICE_ERROR;
        } finally {
            if (locked) {
                String releaseLua = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                redisUtil.executeLua(releaseLua, Collections.singletonList(lockKeyEmail), Collections.singletonList(lockValue));
            }
        }
    }

    @Override
    @Synchronized
    public ResponseResult<?> sendCaptcha(String email) {
        //1.判断邮箱是否为空,是不是合法的邮箱
        if (StringUtils.isEmpty(email) || !email.matches(Values.EMAIL_FORMAT)) {
            LogUtil.info("邮箱为空 或 邮箱格式不正确");
            return ResponseResult.EMAIL_FORMAT_ERROR;
        }

        synchronized (RecruitRegisterServiceImpl.class) {
            //判断验证码是否过期
            String isExistenceCode = redisUtil.getRedisString(Values.REDIS_RECRUIT_CAPTCHA_PREFIX + email);
            if (StringUtils.isNotEmpty(isExistenceCode)) {
                return ResponseResult.EMAIL_CAPTCHA_FOUND;
            }
            //发送并获取验证码
            String code = EmailUtil.getCode(email, javaMailSender);
            //存入redis中，以 Values.REDIS_RECRUIT_CAPTCHA_PREFIX + email为键，验证码为值，设置验证码为 10 分钟
            redisUtil.setRedisStringWithOutTime(Values.REDIS_RECRUIT_CAPTCHA_PREFIX + email, code,10 * 60 * 1000L);
            log.info(email + "：验证码发送成功！" +  code);
            return ResponseResult.EMAIL_CAPTCHA_SEND_SUCCESS;
        }
    }

    @Override
    public ResponseResult<?> listAllClass() {
        //TO DO 优化，从redis里查询
        //先构建Wrapper对象
        LambdaQueryWrapper<RecruitClass> wrapper = new QueryWrapper<RecruitClass>()
                .lambda().eq(RecruitClass::getIsDeleted, 0)
                .select(RecruitClass::getClazz);
        //查询所有班级
        List<String> clazzList = recruitClassMapper.selectObjs(wrapper).stream()
                .map(Object::toString)
                .collect(Collectors.toList());
        return ResponseResult.OK.put(clazzList);
    }

    @Override
    public ResponseResult<?> getModel() {
        return ResponseResult.OK.put("http://123.57.144.143:9000/browser/wlgzs-official-website/static/recruit_user_model.docx");
    }
}
