package weilai.team.officialWebSiteApi.entity.admin.DTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * ClassName:UsernamePasswordDTO
 * @Author:独酌
 * @Create:2024/11/11 11:55
 */
@Data
public class AccountPasswordDTO {

    @ApiModelProperty(value = "用户名/邮箱",required = true)
    @NotBlank(message = "用户名不能为空")
    private String account;

    @ApiModelProperty(value = "密码",required = true)
    @NotBlank(message = "密码不能为空")
    private String password;
}
