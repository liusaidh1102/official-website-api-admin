package weilai.team.officialWebSiteApi.entity.admin.DTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * ClassName:SetUserGroupLadel
 * Description:
 *
 * @Author:独酌
 * @Create:2024/12/3 21:38
 */
@Data
public class SetUserGroupLadle {

    @ApiModelProperty(value = "用户id",required = true)
    @NotNull(message = "用户id不能为空")
    private Long userId;

    @ApiModelProperty(value = "组别",example = "2024$2",required = true)
    @NotBlank(message = "组别不能为空")
    private String group;
}
