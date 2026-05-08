package weilai.team.officialWebSiteApi.entity.admin.DTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName:UserCourseBO
 * Description:
 * 本类用于内部数据的转换
 * @Author:独酌
 * @Create:2024/11/23 8:30
 */
@Data
@NoArgsConstructor //空参构造器，用于在反序列化时使用，如从JSON字符串反序列化对象时
public class UserCourseBO {

    public UserCourseBO(UserCourseDTO userCourseDTO) {
        oneCourseId = System.currentTimeMillis();
        courseName = userCourseDTO.getCourseName();
        weeks = userCourseDTO.getWeeks();
        courseTime = userCourseDTO.getCourseTime();
        coursePlace = userCourseDTO.getCoursePlace();
    }

    private Long oneCourseId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程周数
     */
    private String weeks;

    /**
     * 上课时间
     */
    private String courseTime;

    /**
     * 课程地点
     */
    private String coursePlace;
}
