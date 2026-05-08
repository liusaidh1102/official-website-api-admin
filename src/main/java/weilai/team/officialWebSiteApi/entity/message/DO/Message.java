package weilai.team.officialWebSiteApi.entity.message.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 消息表
 * @TableName message
 */
@TableName(value ="message")
@Data
public class Message implements Serializable {
    /**
     * 消息ID
     */
    @TableId(type = IdType.AUTO)
    private Long messageId;

    /**
     * 发送者用户ID
     */
    private Long senderId;

    /**
     * 接收者用户ID
     */
    private Long receiverId;

    /**
     * 帖子ID
     */
    private Long postId;

    /**
     * 消息类型ID
     */
    private Integer messageType;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息状态（已读、未读）
     */
    private Integer status;

    /**
     * 消息发送时间
     */
    private Date createdAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 41645621321236L;

    public Message(Long senderId, Long receiverId, Long postId, Integer messageType, String content) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.postId = postId;
        this.messageType = messageType;
        this.content = content;
    }

    public Message(){

    }
}