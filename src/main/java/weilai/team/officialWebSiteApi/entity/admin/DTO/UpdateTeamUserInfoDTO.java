package weilai.team.officialWebSiteApi.entity.admin.DTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * ClassName:UpdateTeamUserInfoDTO
 * Description:
 *
 * @Author:独酌
 * @Create:2024/12/2 19:25
 */
@Data
public class UpdateTeamUserInfoDTO {

    @ApiModelProperty(value = "用户id",required = true)
    private Long id;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "组别")
    private String group;

    @ApiModelProperty(value = "年级")
    private String grade;

    @ApiModelProperty(value = "班级")
    private String clazz;

    @ApiModelProperty(value = "学号")
    private String studyId;
}
