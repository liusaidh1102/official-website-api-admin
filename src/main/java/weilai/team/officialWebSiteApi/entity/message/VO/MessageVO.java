package weilai.team.officialWebSiteApi.entity.message.VO;

import lombok.Data;
import weilai.team.officialWebSiteApi.entity.message.DO.Message;
import weilai.team.officialWebSiteApi.entity.message.DTO.MessagePostDTO;

import java.io.Serializable;
import java.util.Date;

@Data
public class MessageVO implements Serializable {

    /**
     * 消息ID
     */
    private Long messageId;

    /**
     * 发送者用户ID
     */
    private Long senderId;

    /**
     * 发送者用户名
     */
    private String username;

    /**
     *发送者用户头像
     */
    private String headPortrait;

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


    private static final long serialVersionUID = 4297707492L;

    public MessageVO(Message message, String username, String postTitle,String headPortrait){
        if(message.getContent()!=null){
            this.content=message.getContent();
        }
        this.messageType=message.getMessageType();
        this.senderId=message.getSenderId();
        this.createdAt=message.getCreatedAt();
        this.postId=message.getPostId();
        this.messageId=message.getMessageId();
        this.postTitle=postTitle;
        this.username=username;
        this.headPortrait=headPortrait;
    }

    public MessageVO(Long messageId, Long senderId, String username, String headPortrait, Long postId, String postTitle, String content, Date createdAt, Integer messageType) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.username = username;
        this.headPortrait = headPortrait;
        this.postId = postId;
        this.postTitle = postTitle;
        this.content = content;
        this.createdAt = createdAt;
        this.messageType = messageType;
    }

    public MessageVO(MessagePostDTO messagePostDTO,String username,String headPortrait){
        this.headPortrait=headPortrait;
        this.username=username;
        this.messageType=messagePostDTO.getMessageType();
        this.content=messagePostDTO.getContent();
        this.createdAt=messagePostDTO.getCreatedAt();
        this.postTitle= messagePostDTO.getPostTitle();
        this.postId= messagePostDTO.getPostId();
        this.messageId=messagePostDTO.getMessageId();
        this.senderId=messagePostDTO.getSenderId();
    }
}
