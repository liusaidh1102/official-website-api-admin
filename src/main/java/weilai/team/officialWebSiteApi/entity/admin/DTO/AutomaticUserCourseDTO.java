package weilai.team.officialWebSiteApi.entity.admin.DTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * ClassName:AutomaticUserCourseDTO
 * Description:
 *
 * @Author:独酌
 * @Create:2025/9/2 18:23
 */
@Data
@AllArgsConstructor
public class AutomaticUserCourseDTO {

    private MultipartFile courseHtml;

    private Date startTime;
}
