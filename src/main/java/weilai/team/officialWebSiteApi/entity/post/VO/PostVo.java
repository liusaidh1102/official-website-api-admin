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
public class PostVo implements Serializable {

    @ApiModelProperty("发帖用户id")
    private Long userId;

    @ApiModelProperty("用户名")
    private String name;

    @ApiModelProperty("用户头像")
    private String headPortrait;

    @ApiModelProperty("帖子分类")
    private Integer type;

    @ApiModelProperty("收藏数")
    private Integer collectCount;

    @ApiModelProperty("点赞数")
    private Integer likeCount;

    @ApiModelProperty("评论数")
    private Integer commentCount;

    @ApiModelProperty("访问量")
    private Integer viewCount;

    @ApiModelProperty("发布时间,格式为YYYY-MM-DD HH:MM:SS")
    private Date postTime;


    @ApiModelProperty("贴子标题(字符 <= 30)")
    private String title;

    @ApiModelProperty("贴子摘要")
    private String postAbstract;


    @ApiModelProperty("贴子文本内容(文本 <= 65535)")
    private String postTxt;

    @ApiModelProperty("帖子标签")
    private List<String> postTags;

    @ApiModelProperty("是否点赞")
    private Boolean isLike;

    @ApiModelProperty("是否收藏")
    private Boolean isCollect;

    @TableField(exist = false)
    private static final long serialVersionUID = 15645625L;
}