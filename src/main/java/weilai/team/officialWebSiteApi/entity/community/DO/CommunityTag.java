package weilai.team.officialWebSiteApi.entity.community.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * @TableName community_tag
 */
@TableName(value ="community_tag")
@Data
public class CommunityTag implements Serializable {
    @TableId
    @ApiModelProperty(value="标签id",required = true)
    private Long tid;
    @ApiModelProperty("标签名称")
    private String tagName;
    @ApiModelProperty("标签的使用次数,默认为0")
    private Integer tagUses;


    private static final long serialVersionUID = 1L;
}