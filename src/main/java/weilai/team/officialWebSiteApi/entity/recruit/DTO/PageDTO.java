package weilai.team.officialWebSiteApi.entity.recruit.DTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author lzw
 * @date 2024/11/30 20:24
 * @description 作用：
 */
@Data
@ApiModel("招新分页参数")
@AllArgsConstructor
@NoArgsConstructor
public class PageDTO {

    @ApiModelProperty(value = "页码（默认是1）",required = true)
    private Long pageNo;

    @ApiModelProperty(value = "每页数量（默认是10）",required = true)
    private Long pageSize;

    public void setPageNo(Long pageNo) {
        if (pageNo != null){
            this.pageNo = pageNo;
            return;
        }
        this.pageNo = 1L;
    }

    public void setPageSize(Long pageSize) {
        if (pageSize != null){
            this.pageSize = pageSize;
            return;
        }
        this.pageSize = 10L;
    }
}
