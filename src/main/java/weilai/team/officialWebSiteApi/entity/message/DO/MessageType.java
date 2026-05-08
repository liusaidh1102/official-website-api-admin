package weilai.team.officialWebSiteApi.entity.message.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 消息类型表
 * @TableName message_type
 */
@TableName(value ="message_type")
@Data
public class MessageType implements Serializable {
    /**
     * 消息类型ID
     */
    @TableId(type = IdType.AUTO)
    private Long typeId;

    /**
     * 消息类型名称（点赞、评论,收藏等）
     */
    private String typeName;

    /**
     * 创建时间
     */
    private Date createdAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 464659851123L;
}