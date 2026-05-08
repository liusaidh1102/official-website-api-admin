package weilai.team.officialWebSiteApi.entity.post.DTO;


import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
 * 分页所需条件
 */
@Data
public class PageDto implements Serializable {

    @ApiModelProperty(value = "每页的条数",required = true)
    private Integer pageSize;

    @ApiModelProperty(value = "页数",required = true)
    private Integer page;

    @TableField(exist = false)
    private List<Long> ids;

    @TableField(exist = false)
    private static final long serialVersionUID = 15645625L;
}
