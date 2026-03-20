package weilai.team.officialWebSiteApi.entity.admin.DTO;

import lombok.Data;

import java.util.Date;

/**
 * ClassName:UserCourseMinDTO
 * Description:
 *
 * @Author:独酌
 * @Create:2025/9/1 20:33
 */
@Data
public class UserCourseMinDTO {
    Long useId;

    String courseHtml;

    Date startTime;
}
