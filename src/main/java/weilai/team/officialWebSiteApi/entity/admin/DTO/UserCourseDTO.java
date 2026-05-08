package weilai.team.officialWebSiteApi.entity.admin.DTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * ClassName:UserCourseDTO
 * Description:
 *
 * @Author:独酌
 * @Create:2024/11/23 7:53
 */
@Data
public class UserCourseDTO {

    /**
     * 星期
     */
    @ApiModelProperty(value = "星期[0,1,2,3,4,5,6]",required = true)
    @NotNull(message = "星期不能为空")
    private Integer weekTime;

    /**
     * 课程名称
     */
    @ApiModelProperty(value = "课程名称",required = true)
    @NotBlank(message = "课程名称不能为空")
    @Size(max = 10,min = 2,message = "课程名称不能超过100个字符")
    private String courseName;

    /**
     * 课程周数
     */
    @ApiModelProperty(value = "课程周数",required = true)
    @NotBlank(message = "课程周数不能为空")
    @Size(max = 10,min = 2,message = "课程周数不能超过10个字符")
    private String weeks;

    /**
     * 上课时间
     */
    @ApiModelProperty(value = "上课时间",required = true)
    @NotBlank(message = "上课时间不能为空")
    private String courseTime;

    /**
     * 课程地点
     */
    @ApiModelProperty(value = "课程地点",required = true)
    @NotBlank(message = "课程地点不能为空")
    @Size(max = 10,message = "课程地点不能超过10个字符")
    private String coursePlace;

    /**
     * 课程id
     */
    @ApiModelProperty(value = "课程id[仅当修改时使用]")
    private Long oneCourseId;
}
