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
import java.util.List;
import java.util.stream.Collectors;

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



    @Transactional
    @Override
    public ResponseResult<?> recruitRegister(RecruitFormDTO recruitFormDTO) {
        //1.先进行参数校验
        if (ObjectUtils.isEmpty(recruitFormDTO)) {
            return ResponseResult.PARAM_IS_NOT_VALID;
        }
        String sex = recruitFormDTO.getSex().trim();
        String email = recruitFormDTO.getEmail().trim();
        String studentId = recruitFormDTO.getStudentId().trim();
        String code = recruitFormDTO.getCode().trim();
        String qqNumber = recruitFormDTO.getQqNumber().trim();

        if (!sex.matches(Values.SEX_FORMAT)) {
            return ResponseResult.SEX_IS_NOT_VALID;
        }
        if (!studentId.matches(Values.STUDENT_ID_FORMAT)) {
            return ResponseResult.STUDENT_ID_IS_NOT_VALID;
        }
        if (!email.matches(Values.EMAIL_FORMAT)) {
            return ResponseResult.EMAIL_FORMAT_ERROR;
        }

        //判断qq号是不是5-10位数
        if (!qqNumber.matches(Values.QQ_NUMBER_FORMAT)) {
            return ResponseResult.QQ_NUMBER_IS_NOT_VALID;
        }

        // 获取文件
        MultipartFile file1 = recruitFormDTO.getFile1();
        MultipartFile file2 = recruitFormDTO.getFile2();


        //判断是不是图片文件
        if (file1.isEmpty() || !FileUtil.isExpectedFileType(file1, FileTypeEnum.IMAGE) || file2.isEmpty() || !FileUtil.isExpectedFileType(file2, FileTypeEnum.IMAGE)) {
            return ResponseResult.FILE_TYPE_ERROR;
        }
        log.info(email + "：图片识别正确！");

        //判断文件的大小是不是符合要求
        if (!FileUtil.isExpectedFileSize(file1, Values.RECRUIT_FILE_MAX_SIZE) || !FileUtil.isExpectedFileSize(file2, Values.RECRUIT_FILE_MAX_SIZE)){
            return ResponseResult.FILE_SIZE_ERROR;
        }
        log.info(email + "：文件大小正确！");

        //判断验证码是否正确，获取redis里面的验证码
        String redisCode = redisUtil.getRedisString(Values.REDIS_RECRUIT_CAPTCHA_PREFIX + email);
        if (!StringUtils.equals(redisCode, code)) {
            //返回验证码错误的信息
            return ResponseResult.EMAIL_CAPTCHA_FILE;
        }

        log.info(email + "：验证码识别正确！");

        //2.进行对象的复制，将前端传过来的DTO复制为DO
        RecruitUser recruitUser = new RecruitUser();
        BeanUtils.copyProperties(recruitFormDTO, recruitUser);
        //3.判断是不是已经报过名了
        // 3.1.构建条件
        LambdaQueryWrapper<RecruitUser> wrapper = new QueryWrapper<RecruitUser>().lambda()
                //学号已经存在，说明就是报名过了
                .eq(RecruitUser::getStudentId, studentId)
                .or()
                //邮箱已经存在，说明就是报名过了
                .eq(RecruitUser::getEmail, email)
                .eq(RecruitUser::getIsDeleted,0);


        //3.2判断是否已经报名
        if (recruitMapper.exists(wrapper)) {
            return ResponseResult.ALREADY_REGISTER;
        }

        log.info(email + "：没有报名过正确！");

        //获取当前的时间错
        long time = System.currentTimeMillis();
//        String file1Url = minioUtil.uploadFile(RECRUIT.getDirName(), studentId + "_" + time + "_1." + FileUtil.getFileSuffix(file1.getOriginalFilename()),file1);
//        String file2Url = minioUtil.uploadFile(RECRUIT.getDirName(), studentId + "_" + time + "_2." + FileUtil.getFileSuffix(file2.getOriginalFilename()),file2);

        String url = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = converter.convertTwoImagesToPdf(file1.getInputStream(), file2.getInputStream());
            url = minioUtil.uploadFileByte(RECRUIT.getDirName(), studentId + "_" + time + ".pdf", byteArrayOutputStream);
        } catch (IOException e) {
            LogUtil.Error("文件上传失败",e);
            return ResponseResult.SERVICE_ERROR;
        }

        log.info(email + "：文件上传成功 " + url);

//        if(file1Url == null || file2Url == null){
//            LogUtil.info("文件上传失败");
//            return ResponseResult.SERVICE_ERROR;
//        }
//        recruitUser.setFileUrl1(file1Url);
//        recruitUser.setFileUrl2(file2Url);
        recruitUser.setFileUrl(url);

        int rows = recruitMapper.insert(recruitUser);
        if (rows > 0) {
            log.info(email + "：插入数据库成功！");
            return ResponseResult.OK;
        } else {
            LogUtil.info("插入数据库失败");
            // 数据库插入失败，返回失败信息
            return ResponseResult.SERVICE_ERROR;
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
