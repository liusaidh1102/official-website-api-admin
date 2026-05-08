package weilai.team.officialWebSiteApi.entity.admin.DTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * ClassName:ModifyManyUser
 * Description:
 *
 * @Author:独酌
 * @Create:2024/12/7 9:23
 */
@Data
public class ModifyManyUserDTO {
    @ApiModelProperty(value = "用户id",required = true)
    @NotNull(message = "用户id不能为空")
    private Long[] ids;

    @ApiModelProperty(value = "组别")
    @NotBlank(message = "组别不能为空")
    private String group;

    @ApiModelProperty(value = "年级")
    @NotBlank(message = "年级不能为空")
    private String grade;

    @ApiModelProperty(value = "班级")
    @NotBlank(message = "班级不能为空")
    private String clazz;
}
