package weilai.team.officialWebSiteApi.entity.recruit.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author lzw
 * @date 2024/12/2 17:18
 * @description 作用： 要面试人员的信息
 */
@Data
public class RecruitInterviewVO implements Serializable {

    @ApiModelProperty("面试记录的id")
    private Long id;

    @ApiModelProperty("面试人的id")
    private  Integer userId;

    @ApiModelProperty("面试人的姓名")
    private String name;

    @ApiModelProperty("面试的地点")
    private String place;

    @ApiModelProperty("面试轮次")
    private Integer round;

    @ApiModelProperty("年级")
    private String grade;

    @ApiModelProperty("开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime startTime;

    @ApiModelProperty("结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime endTime;

    @ApiModelProperty("面试的状态")
    private Integer interviewStatus;

    @ApiModelProperty("第一个面试管的信息")
    private HrVO firstHr;

    @ApiModelProperty("第二个面试管的信息")
    private HrVO secondHr;

    @ApiModelProperty("第三个面试管的信息")
    private HrVO thirdHr;

}
