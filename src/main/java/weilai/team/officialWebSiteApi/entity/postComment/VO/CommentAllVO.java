package weilai.team.officialWebSiteApi.entity.postComment.VO;


import lombok.Data;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * ClassName:CommentAllVO
 * Description:
 *
 * @Author:独酌
 * @Create:2024/11/16 17:15
 */
@Data
public class CommentAllVO{

    private Long commentId;

    private Long userId;

    private Long pointUser;

    private String commentTxt;

    private Date commentTime;

    private Long likeCount;

    private Boolean isLike;

    private Boolean isMyComment;

}
