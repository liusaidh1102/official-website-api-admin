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

/**
 * ClassName:ReplyCommentDTO
 * Description:
 *
 * @Author:独酌
 * @Create:2024/11/15 16:08
 */
@Data
@Slf4j
public class ReplyCommentDTO {
    @ApiModelProperty(value = "一级评论id",required = true)
    @NotNull(message = "一级评论id不能为空")
    private Long commentId;

    @ApiModelProperty(value = "@用户id",required = true)
    @NotNull(message = "@用户id不能为空")
    private Long userId;

    @ApiModelProperty("回复评论内容")
    @NotBlank(message = "回复评论内容不能为空")
    private String commentTxt;

    public void setCommentTxt(String commentTxt) {
        this.commentTxt = Base64CompressUtil.ownCompressImage(commentTxt);
    }
}
