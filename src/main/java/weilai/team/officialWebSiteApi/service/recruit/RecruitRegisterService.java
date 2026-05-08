package weilai.team.officialWebSiteApi.service.recruit;

import org.springframework.web.multipart.MultipartFile;
import weilai.team.officialWebSiteApi.entity.recruit.DTO.RecruitFormDTO;
import weilai.team.officialWebSiteApi.util.ResponseResult;

/**
 * @author lzw
 * @date 2024/11/11 15:05
 * @description 作用：招新的报名相关业务
 */
public interface RecruitRegisterService {


    /**
     * 招新报名
     * @param recruitFormDTO 报名的表单
     * @return 无返回值
     */
    ResponseResult<?> recruitRegister(RecruitFormDTO recruitFormDTO);


    /**
     * 招新表单发送验证码
     * @param email 邮箱
     * @return 返回验证码
     */
    ResponseResult<?> sendCaptcha(String email);

    /**
     * 获取所有支持报名的班级
     * @return 返回所有的班级
     */
    ResponseResult<?> listAllClass();

    ResponseResult<?> getModel();
}
