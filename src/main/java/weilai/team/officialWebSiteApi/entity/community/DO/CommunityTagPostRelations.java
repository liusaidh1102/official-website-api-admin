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
 * @TableName community_tag_post_relations
 */
@TableName(value ="community_tag_post_relations")
@Data
public class CommunityTagPostRelations implements Serializable {
    @TableId
    @ApiModelProperty("主键id")
    private Long relationId;
    @ApiModelProperty("标签id")
    private Long tagId;
    @ApiModelProperty("帖子id")
    private Long postId;
    @ApiModelProperty("添加文章的标签类型")
    private Integer type;
    private static final long serialVersionUID = 1L;
}