package weilai.team.officialWebSiteApi.entity.recruit.DTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author lzw
 * @date 2024/11/11 22:38
 * @description 作用：招新报名的表单
 */
@Data
@ApiModel("招新的表单")
@AllArgsConstructor
@NoArgsConstructor
public class RecruitFormDTO {

    /*
    姓名
     */
    @ApiModelProperty(value = "姓名", required = true)
    @NotBlank(message = "姓名不能为空")
    private String name;

    /*
    班级
     */
    @ApiModelProperty(value = "班级(下拉框选择)", required = true)
    @NotBlank(message = "班级不能为空")
    private String clazz;

    /*
    学号
     */
    @ApiModelProperty(value = "学号11位", required = true)
    @NotBlank(message = "学号不能为空")
    private String studentId;

    /*
    性别男或女
     */
    @ApiModelProperty(value = "性别男或女", required = true)
    @NotBlank(message = "性别不能为空")
    private String sex;

    /*
    qq号
     */
    @ApiModelProperty(value = "qq号码", required = true)
    @NotBlank(message = "QQ号码不能为空")
    private String qqNumber;

    /*
    邮箱
     */
    @ApiModelProperty(value = "邮箱", required = true)
    @NotBlank(message = "邮箱不能为空")
    private String email;


    /*
    验证码
     */
    @ApiModelProperty(value = "验证码", required = true)
    @NotBlank(message = "验证码不能为空")
    private String code;


    @ApiModelProperty(value = "简历正面", required = true)
    @NotNull
    private MultipartFile file1;

    @ApiModelProperty(value = "简历反面", required = true)
    @NotNull
    private MultipartFile file2;



//    @ApiModelProperty(value = "年级", required = true)
//    @NotNull
//    private String grade;
}
