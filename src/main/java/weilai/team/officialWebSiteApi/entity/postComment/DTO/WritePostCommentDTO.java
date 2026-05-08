package weilai.team.officialWebSiteApi.entity.postComment.DTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import weilai.team.officialWebSiteApi.util.Base64CompressUtil;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * ClassName:WriteCommentDTO
 * Description:
 *
 * @Author:独酌
 * @Create:2024/11/15 15:18
 */
@Data
@Slf4j
public class WritePostCommentDTO {

    @ApiModelProperty(value = "贴子id",required = true)
    @NotNull(message = "贴子id不能为空")
    private Long postId;

    @ApiModelProperty(value = "评论",required = true)
    @NotBlank(message = "评论不能为空")
    private String commentTxt;

    public void setCommentTxt(String commentTxt) {
        this.commentTxt = Base64CompressUtil.ownCompressImage(commentTxt);
    }
}
