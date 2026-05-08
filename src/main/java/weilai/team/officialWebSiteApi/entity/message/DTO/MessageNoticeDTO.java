package weilai.team.officialWebSiteApi.entity.message.DTO;

import lombok.Data;
import weilai.team.officialWebSiteApi.entity.post.DTO.PostNoticeDto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class MessageNoticeDTO implements Serializable {
    private  Long noticeId;

    private  String title;

    private String content;

    private Date createAt;

    private Long senderId;

    private  String username;

    private String headPortrait;

    private static final long serialVersionUID = 742976470743692L;

    public MessageNoticeDTO(PostNoticeDto postNoticeDto, String username, String headPortrait){
        this.content=postNoticeDto.getContent();
        this.senderId= postNoticeDto.getUserId();
        this.noticeId= postNoticeDto.getNoticeId();
        this.title=postNoticeDto.getTitle();
        this.createAt=postNoticeDto.getCreateAt();
        this.username=username;
        this.headPortrait=headPortrait;
    }

    public MessageNoticeDTO(Long noticeId, String title, String content, Date createAt, Long senderId, String username, String headPortrait) {
        this.noticeId = noticeId;
        this.title = title;
        this.content = content;
        this.createAt = createAt;
        this.senderId = senderId;
        this.username = username;
        this.headPortrait = headPortrait;
    }
}
