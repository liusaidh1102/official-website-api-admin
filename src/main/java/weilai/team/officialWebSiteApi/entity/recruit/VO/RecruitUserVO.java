package weilai.team.officialWebSiteApi.entity.recruit.VO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * @author lzw
 * @date 2024/11/21 16:36
 * @description 作用： 返回用户的信息
 */
@Data
public class RecruitUserVO {
    @ApiModelProperty("记录的id，主键自增")
    private Long id;


    @ApiModelProperty("用户的userId")
    private Long userId;
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
    /**
     * 报名要的word文件的地址
     */
    @ApiModelProperty("报名要的word文件的地址")
    private String fileUrl;

    /**
     * 报名状态0待安排1报名失败2待面试3面试通过4待二面5淘汰
     */
    @ApiModelProperty("0代表待安排；1代表待面试；2代表已录取；3代表未录")
    private Integer status;
}
