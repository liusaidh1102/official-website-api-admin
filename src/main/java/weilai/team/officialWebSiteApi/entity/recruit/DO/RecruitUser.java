package weilai.team.officialWebSiteApi.entity.recruit.DO;
import java.io.Serializable;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * 报名的用户
 *
 * @TableName recruit_user
 */
@TableName("recruit_user")
@Data
public class RecruitUser implements Serializable {

    /**
     * 报名用户id，主键自增
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty("报名用户id，主键自增")
    private Long id;
    /**
     * 姓名
     */
    @ApiModelProperty("姓名")
    private String name;
    /**
     * 年级
     */
    @ApiModelProperty("年级")
    private String grade;
    /**
     * 班级
     */
    @ApiModelProperty("班级")
    private String clazz;
    /**
     * 学号
     */
    @ApiModelProperty("学号")
    private String studentId;
    /**
     * 性别
     */
    @ApiModelProperty("性别")
    private String sex;
    /**
     * QQ
     */
    @ApiModelProperty("QQ")
    private String qqNumber;
    /**
     * 邮箱
     */
    @ApiModelProperty("邮箱")
    private String email;
//    /**
//     * 报名要的word文件的地址
//     */
//    @ApiModelProperty("简历正面")
//    private String fileUrl1;
//
//
//    @ApiModelProperty("简历反面")
//    private String fileUrl2;

    @ApiModelProperty("简历pdf")
    private String fileUrl;

    /**
     * 投递简历的时间
     */
    @ApiModelProperty("投递简历的时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss")
    private LocalDateTime createTime;
    /**
     * 上次状态修改的时间
     */
    @ApiModelProperty("上次状态修改的时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss")
    private LocalDateTime updateTime;
    /**
     * 是否删除，0表示未删除，1表示已删除,加上@TableField("isDeleted")，否则mybatis-plus会将他识别为deleted
     */
    @TableField("is_deleted")
    @ApiModelProperty("是否删除，0表示未删除，1表示已删除")
    private Integer isDeleted;
    /**
     * 报名用户的状态，0代表待安排；1代表待面试；2代表已录取；3代表未录取
     */
    @ApiModelProperty("报名用户的状态，0代表待安排；1代表待面试；2代表已录取；3代表未录取")
    private Integer status;


}
