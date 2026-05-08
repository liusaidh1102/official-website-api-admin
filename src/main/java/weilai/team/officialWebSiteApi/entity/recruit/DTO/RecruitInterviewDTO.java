package weilai.team.officialWebSiteApi.entity.recruit.DTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
/**
 * @author lzw
 * @date 2024/12/3 17:20
 * @description 作用： 面试页面的 查询参数
 */
@Data
@ApiModel("面试页面的 查询参数")
public class RecruitInterviewDTO {

    @ApiModelProperty("年级")
    private String grade;

    @ApiModelProperty("姓名查询")
    private String name;

    @ApiModelProperty("面试的轮次(数字1代表一面、2代表二面，不填是所有)")
    private Integer round;

    @ApiModelProperty(value = "面试的状态（-1查询全部，0查询待我面试，1待反馈；2已录取；3未录取）",required = true)
    @NotNull
    private Integer status;

    @ApiModelProperty("面试官的id（可以多个）")
    private List<Long> ids;

    @ApiModelProperty("查询开始的时间yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;

    @ApiModelProperty("查询结束的时间yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;

}
