package weilai.team.officialWebSiteApi.entity.admin.VO;

import lombok.Data;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * ClassName:UserPostDTO
 * Description:
 *
 * @Author:独酌
 * @Create:2024/11/20 18:52
 */
@Data
public class UserPostVO {

    /**
     * 贴子id
     */
    private Long postId;

    /**
     * 收藏id
     */
    private Long collectId;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 发布时间
     */
    private Date putTime;

    /**
     * 标题
     */
    private String title;

    /**
     * 贴子的大概内容
     */
    private String postAbstract;

    /**
     * 点赞数
     */
    private Integer postLikeCount;

    /**
     * 评论数（一级评论 + 多级评论数）
     */
    private Integer commentCount;

    /**
     * 收藏数
     */
    private Integer collectCount;

    /**
     * 标签
     */
    private List<String> tags;
}
