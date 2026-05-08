package weilai.team.officialWebSiteApi.entity.post.VO;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
@Data
@AllArgsConstructor
public class PutPostVo implements Serializable {
    @ApiModelProperty("贴子id")
    private Long postId;
    @TableField(exist = false)
    private static final long serialVersionUID = 15645625L;
}
