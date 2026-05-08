package weilai.team.officialWebSiteApi.entity.postComment.VO;

import lombok.Data;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * ClassName:CommentOne
 * Description:
 * 内部数据转换层
 * @Author:独酌
 * @Create:2024/11/16 11:26
 */
@Data
public class CommentOneVO {

    private Long commentId;

    private Long userId;

    private String commentTxt;

    private Date commentTime;

    private Long likeCount;

    private Integer commentCount;

    private Boolean isLike;

    private Boolean isMyComment;
}
