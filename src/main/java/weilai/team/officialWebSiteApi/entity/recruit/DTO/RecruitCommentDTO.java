package weilai.team.officialWebSiteApi.entity.recruit.DTO;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author lzw
 * @date 2024/12/7 12:09
 * @description 作用：
 */
@ApiModel("面试官评价")
@Data
public class RecruitCommentDTO {

    @NotNull
    @ApiModelProperty("面试记录的id")
    private  Long id;

    @NotNull
    @ApiModelProperty("用户的id")
    private  Long userId;

    @NotBlank
    @ApiModelProperty("面试官的评价")
    private  String comment;
    @NotNull
    @ApiModelProperty("2已录取；3未录取）")
    private  Integer status;

    @ApiModelProperty("是不是待二面，0不是，1是")
    @NotNull
    private  Integer isSecond;
}
