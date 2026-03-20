package weilai.team.officialWebSiteApi.entity.admin.DTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * ClassName:AddUserDTO
 * Description:
 *
 * @Author:独酌
 * @Create:2024/12/4 17:07
 */
@Data
public class AddUserDTO {

    @ApiModelProperty(value = "用户名",required = true)
    @NotBlank(message = "用户名不能为空")
    private String name;

    @ApiModelProperty(value = "学号",required = true)
    @NotBlank(message = "学号不能为空")
    private String studyId;

    @ApiModelProperty(value = "邮箱",required = true)
    @NotBlank(message = "邮箱不能为空")
    private String email;

    @ApiModelProperty(value = "性别",required = true)
    @NotBlank(message = "性别不能为空")
    private String sex;

    @ApiModelProperty(value = "年级",required = true)
    @NotBlank(message = "年级不能为空")
    private String grade;

    @ApiModelProperty(value = "班级(计科231)",required = true)
    @NotBlank(message = "班级不能为空")
    private String clazz;

    @ApiModelProperty(value = "组别",required = true)
    @NotBlank(message = "组别不能为空")
    private String group;

    @ApiModelProperty(value = "电话",required = true)
    @NotBlank(message = "电话不能为空")
    private String phone;

    @ApiModelProperty(value = "QQ",required = true)
    @NotBlank(message = "QQ不能为空")
    private String QQ;
}
