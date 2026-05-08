package weilai.team.officialWebSiteApi.entity.admin.DTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * ClassName:UpdateUserInfoDTO
 * Description:
 *
 * @Author:独酌
 * @Create:2024/11/14 15:53
 */
@Data
public class UpdateUserInfoDTO {

    @ApiModelProperty(value = "手机号")
    @Size(max = 11,message = "电话号码位数不对")
    private String phone;

    @ApiModelProperty(value = "QQ号")
    private String qq;

    @ApiModelProperty(value = "毕业去向")
    @Size(max = 20,message = "毕业去向不能超过100个字符")
    private String graduationDestination;

    @ApiModelProperty(value = "个性签名")
    @Size(max = 50,message = "个性签名不能超过100个字符")
    private String userDestination;

    @ApiModelProperty(value = "蓝桥杯获奖数量")
    @Size(max = Byte.MAX_VALUE,message = "最大值为127")
        private Integer lanQiaoCount;

    @ApiModelProperty(value = "软件著作的数量")
    @Size(max = Byte.MAX_VALUE,message = "最大值为127")
    private Integer copyrightCount;

    @ApiModelProperty(value = "年薪")
    private Integer salaryYear;

    @ApiModelProperty(value = "地区的聚合全称（中国,北京,北京市,东城区）")
    private String area;
}
