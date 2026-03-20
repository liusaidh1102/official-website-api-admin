package weilai.team.officialWebSiteApi.entity.community.DTO;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
public class TagPageDto implements Serializable {
    @ApiModelProperty(value = "每页的条数,默认为10")
    private Integer pageSize=10;

    @ApiModelProperty(value = "页数,默认为第1页")
    private Integer page=1;

    @ApiModelProperty(value = "标签名",required = true)
    private String tagName;

    @TableField(exist = false)
    private static final long serialVersionUID = 15645625L;

    public void setPage(Integer page) {
        if (page != null) {
            this.page = page;
        } else {
            this.page = 1;
        }
    }

    public void setPageSize(Integer pageSize) {
        if (pageSize != null) {
            this.pageSize = pageSize;
        } else {
            this.pageSize = 10;
        }
    }
}
