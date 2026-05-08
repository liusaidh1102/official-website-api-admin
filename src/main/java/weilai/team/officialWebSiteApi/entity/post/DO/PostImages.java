package weilai.team.officialWebSiteApi.entity.post.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 贴子的图片路径
 * @TableName post_images
 */
@TableName(value ="post_images")
@Data
public class PostImages implements Serializable {
    /**
     * 主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 贴子id
     */
    private Long postId;

    /**
     * 图片url
     */
    private String imageUrl;

    /**
     * 是否删除,0:未删除,1:已删除
     */
    private Integer deleteFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 15645625L;
}