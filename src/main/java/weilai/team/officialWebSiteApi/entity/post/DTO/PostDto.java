package weilai.team.officialWebSiteApi.entity.post.DTO;


import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 前端传来的的贴子内容
 * @TableName post
 */
@Data
public class PostDto implements Serializable {


    @ApiModelProperty(value = "贴子标题(字符 <= 30)",required = true)
    private String title;

    @ApiModelProperty(value = "贴子文本内容(文本 <= 65535",required = true)
    private String postTxt;

    @ApiModelProperty(value = "贴子摘要(字符 <= 100")
    private String postAbstract;

    @ApiModelProperty(value = "类型(1博客|2公告|3交流 |4头脑风暴",required = true)
    private Integer type;


    @ApiModelProperty(value = "帖子的标签")
    private String[] tags;

    @TableField(exist = false)
    private static final long serialVersionUID = 15645625L;
}