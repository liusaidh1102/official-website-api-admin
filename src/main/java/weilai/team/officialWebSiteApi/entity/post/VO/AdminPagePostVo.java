package weilai.team.officialWebSiteApi.entity.post.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class AdminPagePostVo implements Serializable {
    @ApiModelProperty("帖子id")
    private Long id;

    @ApiModelProperty("贴子标题(字符 <= 30)")
   private String title;

    @ApiModelProperty("作者")
    private String name;

    @ApiModelProperty("类型(1博客|2公告|3交流 |4头脑风暴)")
    private String type;

    @ApiModelProperty("发布时间,格式为YYYY-MM-DD HH:MM:SS")
    private Date postTime;

}
