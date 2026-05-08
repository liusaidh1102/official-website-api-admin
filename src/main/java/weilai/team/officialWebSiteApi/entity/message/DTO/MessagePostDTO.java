package weilai.team.officialWebSiteApi.entity.message.DTO;

import lombok.Data;
import weilai.team.officialWebSiteApi.entity.message.DO.Message;

import java.util.Date;

@Data
public class MessagePostDTO {

    /**
     * 消息ID
     */
    private Long messageId;

    /**
     * 发送者用户ID
     */
    private Long senderId;

    /**
     * 帖子ID
     */
    private Long postId;

    /**
     * 帖子标题
     */
    private String postTitle;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息发送时间
     */
    private Date createdAt;

    /**
     * 信息类型
     */
    private Integer messageType;

    private static final long serialVersionUID = 34742976474L;


    public MessagePostDTO(Message message,String postTitle){
        this.messageId=message.getMessageId();
        this.content=message.getContent();
        this.messageType=message.getMessageType();
        this.senderId=message.getSenderId();
        this.postId=message.getPostId();
        this.createdAt=message.getCreatedAt();
        this.postTitle=postTitle;
    }

    public MessagePostDTO(Long messageId, Long senderId, Long postId, String postTitle, String content, Date createdAt, Integer messageType) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.postId = postId;
        this.postTitle = postTitle;
        this.content = content;
        this.createdAt = createdAt;
        this.messageType = messageType;
    }
}
