package weilai.team.officialWebSiteApi.entity.recruit.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.springframework.format.annotation.DateTimeFormat;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author lzw
 * @date 2024/11/15 8:36
 * @description 作用： 候选人页面的查询参数
 */
@ApiModel("查询的参数接口")
@Data
public class RecruitQueryDTO {

    /*
     * 报名用户的状态，0代表待安排；1代表待面试；2代表已录取；3代表未录取
     */
    @ApiModelProperty(value = "报名用户的状态，0代表待安排；1代表待面试；2代表已录取；3代表未录取",required = true)
    @NotNull(message = "状态不能为空")
    private Integer status;

    /*
     * 姓名
     */
    @ApiModelProperty("搜索的条件(姓名，qq号，邮箱，学号)")
    private String condition;


    /*
     * 年级
     */
    @ApiModelProperty("年级")
    private String grade;

    /*
     * 性别
     */
    @ApiModelProperty("性别")
    private String sex;

    /*
     * 班级
     */
    @ApiModelProperty("班级")
    private String clazz;

    @ApiModelProperty("查询的开始时间yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;

    @ApiModelProperty("查询的结束时间yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;

    /*
     * 分页参数
     */
    @ApiModelProperty("分页的参数")
    @NotNull(message = "分页参数不能为空")
    private PageDTO pageDTO;

}
