package weilai.team.officialWebSiteApi.entity.recruit.DTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author lzw
 * @date 2024/12/7 15:02
 * @description 作用：
 */
@ApiModel("结果导出的参数")
@Data
public class ExportDTO {

    /*
     * 报名用户的状态，0代表待安排；1代表待面试；2代表已录取；3代表未录取
     */
    @ApiModelProperty("报名用户的状态，0代表待安排；1代表待面试；2代表已录取；3代表未录取")
    private Integer status;

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


    @ApiModelProperty("筛选的开始时间(格式2023-02-21")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;

    @ApiModelProperty("筛选的结束时间(格式2023-02-21")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
}
