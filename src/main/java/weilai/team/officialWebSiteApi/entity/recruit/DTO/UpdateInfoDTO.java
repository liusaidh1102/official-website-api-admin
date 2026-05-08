package weilai.team.officialWebSiteApi.entity.recruit.DTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author lzw
 * @date 2024/12/2 15:05
 * @description 作用： 修改信息的表单
 */
@Data
@ApiModel("招新修改信息的表单")
public class UpdateInfoDTO {

    @ApiModelProperty("用户的id")
    @NotNull
    private Long id;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("年级")
    private String grade;

    @ApiModelProperty("学号")
    private String studentId;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("性别")
    private String sex;

    @ApiModelProperty("班级")
    private String clazz;

    @ApiModelProperty("qq")
    private String qq;

}
