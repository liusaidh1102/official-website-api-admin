package weilai.team.officialWebSiteApi.entity.recruit.DTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * @author lzw
 * @date 2024/11/25 11:05
 * @description 作用： 安面试官的参数
 */
@ApiModel("安排面试官的参数")
@Data
public class ScheduleInterviewDTO {
    @ApiModelProperty("面试人的id")
    private Long userId;
    @ApiModelProperty("面试官1的id")
    private Long firstHr;
    @ApiModelProperty("面试官2的id")
    private Long secondHr;
    @ApiModelProperty("面试官3的id")
    private Long thirdHr;
    @ApiModelProperty("面试的开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startTime;
    @ApiModelProperty("面试的结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endTime;
    @ApiModelProperty("面试的地点")
    private String place;
}
