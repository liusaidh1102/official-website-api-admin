package weilai.team.officialWebSiteApi.entity.post.DTO;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

/**
 * 修改的贴子内容
 * @TableName post
 */
@Data
public class UpdatePostDto implements Serializable {

    @ApiModelProperty("帖子id")
    private Long id;


    @ApiModelProperty("贴子标题(字符 <= 30)")
    private String title;


    @ApiModelProperty("贴子文本内容(文本 <= 65535)")
    private String postTxt;


    @ApiModelProperty("贴子摘要(字符 <= 100)")
    private String postAbstract;


    @ApiModelProperty("类型(1博客|2公告|3交流 |4头脑风暴)")
    private Integer type;

    @ApiModelProperty("标签")
    private List<String> postTags;

    @TableField(exist = false)
    private static final long serialVersionUID = 15645625L;
}