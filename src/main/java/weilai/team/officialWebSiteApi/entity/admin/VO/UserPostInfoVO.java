package weilai.team.officialWebSiteApi.entity.admin.VO;

import lombok.Data;

/**
 * ClassName:UserPostInfoVO
 * Description:
 *
 * @Author:独酌
 * @Create:2024/11/20 19:40
 */
@Data
public class UserPostInfoVO {
    /**
     * 指定用户的所有收到的点赞数量
     */
    private Integer allLikeCount;

    /**
     * 指定用户的所有的贴子数量
     */
    private Integer allPostCount;

    /**
     * 指定用户的所有收到的收藏数量
     */
    private Integer allCollectCount;

    /**
     * 指定用户的所有收到的评论数量
     */
    private Integer allCommentCount;
}
