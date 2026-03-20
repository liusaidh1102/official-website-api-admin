package weilai.team.officialWebSiteApi.entity.recruit.VO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author lzw
 * @date 2024/11/25 15:18
 * @description 作用：面试官的信息
 */
@Data
public class HrVO {
    /*
     面试官的id
     */
    @ApiModelProperty("面试官的id")
    private Long id;
    /*
     面试官的姓名
     */
    @ApiModelProperty("面试官的姓名")
    private String name;
    /*
     面试官的头像
     */
    @ApiModelProperty("面试管的头像")
    private String headPortrait;
}
