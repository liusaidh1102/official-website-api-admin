package weilai.team.officialWebSiteApi.entity.message.DTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UpdateNoticeDTO {

    @ApiModelProperty("帖子id")
    private Long id;


    @ApiModelProperty("贴子标题(字符 <= 30)")
    private String title;


    @ApiModelProperty("贴子文本内容(文本 <= 65535)")
    private String postTxt;

}
