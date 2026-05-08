package weilai.team.officialWebSiteApi.entity.admin.DTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * ClassName:ModifyUserAuthDTO
 * Description:
 *
 * @Author:独酌
 * @Create:2024/12/3 15:51
 */
@Data
public class ModifyUserAuthDTO {

    @ApiModelProperty("用户id")
    @NotNull(message = "用户id不能为空")
    private Long userId;

    @ApiModelProperty("权限")
    private String[] authority;
}
