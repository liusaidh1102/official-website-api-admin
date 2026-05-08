package weilai.team.officialWebSiteApi.entity.post.DTO;


import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;


/**
 * 分页所需条件
 */
@Data
public class PageQueryDto implements Serializable {

    @ApiModelProperty(value = "每页的条数,默认为10")
    private Integer pageSize=10;

    @ApiModelProperty(value = "页数,默认为第1页")
    private Integer page=1;

    @ApiModelProperty(value = "(0综合|1博客|2公告|3交流 |4头脑风暴)")
    private Integer type;

    @ApiModelProperty(value = "搜索的内容")
    private String condition;

    @ApiModelProperty(value="0、热门1、最新（默认为热门）")
    private Integer sort=0;

    @ApiModelProperty(value = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

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

    public void setSort(Integer sort) {
        if (sort != null) {
            this.sort = sort;
        } else {
            this.sort = 0;
        }
    }
}
