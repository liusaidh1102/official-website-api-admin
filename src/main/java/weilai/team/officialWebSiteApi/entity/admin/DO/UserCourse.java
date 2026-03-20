package weilai.team.officialWebSiteApi.entity.admin.DO;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 用户的课程表
 * @TableName user_course
 */
@TableName(value ="user_course")
@Data
public class UserCourse implements Serializable {
    /**
     * 主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long useId;

    /**
     * 星期一的课表（<=300）
     */
    private String monday;

    /**
     * 星期二的课表（<=300）
     */
    private String tuesday;

    /**
     * 星期三的课表（<=300）
     */
    private String wednesday;

    /**
     * 星期四的课表（<=300）
     */
    private String thursday;

    /**
     * 星期五的课表（<=300）
     */
    private String friday;

    /**
     * 星期六的课表（<=300）
     */
    private String saturday;

    /**
     * 星期日的课表（<=300）
     */
    private String sunday;

    /**
     * 课表html
     */
    private String courseHtml;

    /**
     * 第一周的星期一的日期
     */
    private Date startTime;

    /**
     * 是否删除,0:未删除,1:已删除
     */
    @TableLogic
    private Integer deleteFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 38273492737L;
}