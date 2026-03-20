package weilai.team.officialWebSiteApi.entity.community.DTO;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * className tagUses
 *
 * @Author yrx
 * @Description //TODO
 */
@Data
public class tagUsesDto implements Serializable {
    @ApiModelProperty(value = "标签名称")
    private String tagName;
    @ApiModelProperty(value = "标签的使用次数")
    private Integer count;
    @TableField(exist = false)
    private static final long serialVersionUID=333333333L;

    public tagUsesDto(String tagName, Integer count) {
        this.tagName = tagName;
        this.count = count;
    }

    public tagUsesDto() {
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}
