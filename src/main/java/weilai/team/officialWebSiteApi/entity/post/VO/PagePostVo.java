package weilai.team.officialWebSiteApi.entity.post.VO;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 前端接收的贴子内容
 * @TableName post
 */
@Data
public class PagePostVo implements Serializable {

    @ApiModelProperty("发帖用户id")
    private Long userId;

    @ApiModelProperty("发帖用户名")
    private String name;

    @ApiModelProperty("发帖用户头像")
    private String headPortrait;

    @ApiModelProperty("帖子id")
    private Long id;

    @ApiModelProperty("点赞数")
    private Integer likeCount;

    @ApiModelProperty("评论数")
    private Integer commentCount;

    @ApiModelProperty("访问量")
    private Integer viewCount;

    @ApiModelProperty("贴子标题(字符 <= 30)")
    private String title;

    @ApiModelProperty("贴子摘要(字符 <= 100")
    private String postAbstract;

    @ApiModelProperty("帖子标签")
    private List<String> postTags;

    @ApiModelProperty("是否点赞")
    private Boolean isLike;

     @ApiModelProperty("发布时间,格式为YYYY-MM-DD HH:MM:SS")
    private Date postTime;

     @ApiModelProperty("类型(1博客|2公告|3交流 |4头脑风暴)")
    private String type;

    @TableField(exist = false)
    private static final long serialVersionUID = 15645625L;
}