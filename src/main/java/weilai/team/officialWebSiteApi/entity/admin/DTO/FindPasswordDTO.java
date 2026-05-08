package weilai.team.officialWebSiteApi.entity.admin.DTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * ClassName:FindPasswordDTO
 * Description:
 *
 * @Author:独酌
 * @Create:2024/11/12 8:59
 */
@Data
public class FindPasswordDTO {

    @ApiModelProperty(value = "邮箱",required = true)
    @NotBlank(message = "邮箱不能为空")
    private String email;

    @ApiModelProperty(value = "验证码",required = true)
    @NotBlank(message = "验证码不能为空")
    private String code;

    @ApiModelProperty(value = "新密码",required = true)
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
