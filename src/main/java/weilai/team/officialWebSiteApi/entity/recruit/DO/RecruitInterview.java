package weilai.team.officialWebSiteApi.entity.recruit.DO;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
/**
* 面试评价表
* @TableName recruit_interview
*/
@Data
@TableName("recruit_interview")
public class RecruitInterview implements Serializable {

    /**
    * ID，主键自增
    */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty("ID，主键自增")
    private Long id;
    /**
    * 面试人的ID，与面试人表关联
    */
    @ApiModelProperty("面试人的ID，与面试人表关联")
    private Long userId;
    /**
    * 面试官1,和面试官的id关联
    */
    @ApiModelProperty("面试官1,和面试官的id关联")
    private Long firstHr;
    /**
    * 面试官2，和面试官的id关联
    */
    @ApiModelProperty("面试官2，和面试官的id关联")
    private Long secondHr;
    /**
    * 面试官3，和面试官的id关联
    */
    @ApiModelProperty("面试官3，和面试官的id关联")
    private Long thirdHr;
    /**
    * 面试的开始时间
    */
    @ApiModelProperty("面试的开始时间")
    private LocalDateTime startTime;

    /*
    面试的结束时间
     */
    @ApiModelProperty("面试的结束时间")
    private LocalDateTime endTime;

    /**
    * 面试的地点
    */
    @ApiModelProperty("面试的地点")
    private String place;
    /**
    * 面试的结果：1待反馈；2已录取；3未录取
    */
    @ApiModelProperty("面试的结果：1待反馈；2已录取；3未录取")
    private Integer interviewStatus;
    /**
    * 500字以内的面评
    */
    @ApiModelProperty("500字以内的面评")
    private String comment;


    /*
    面试的轮次：1代表一面；2代表二面
     */
    @ApiModelProperty("面试轮次")
    private Integer round;

    /*
    姓名
     */
    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("年级")
    private String grade;

}
