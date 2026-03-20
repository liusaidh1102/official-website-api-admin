package weilai.team.officialWebSiteApi.entity.message.VO;

import lombok.Data;
import weilai.team.officialWebSiteApi.entity.message.DTO.MessageNoticeDTO;
import weilai.team.officialWebSiteApi.entity.post.DTO.PostNoticeDto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class MessageNoticeVO implements Serializable {

    private  Long noticeId;

    private  String title;

    private String content;

    private Date createAt;

    private Long senderId;

    private  String username;

    private String headPortrait;

    private Integer status;

    private static final long serialVersionUID = 934976743692L;

    public MessageNoticeVO(MessageNoticeDTO messageNoticeDTO, Integer status){
        this.content= messageNoticeDTO.getContent();
        this.senderId= messageNoticeDTO.getSenderId();
        this.noticeId= messageNoticeDTO.getNoticeId();
        this.title= messageNoticeDTO.getTitle();
        this.createAt= messageNoticeDTO.getCreateAt();
        this.headPortrait=messageNoticeDTO.getHeadPortrait();
        this.username= messageNoticeDTO.getUsername();
        this.status=status;
    }

    public MessageNoticeVO(PostNoticeDto postNoticeDto, String username, String headPortrait, int status){
        this.content= postNoticeDto.getContent();
        this.senderId= postNoticeDto.getUserId();
        this.noticeId= postNoticeDto.getNoticeId();
        this.title= postNoticeDto.getTitle();
        this.createAt= postNoticeDto.getCreateAt();
        this.headPortrait=headPortrait;
        this.username= username;
        this.status=status;
    }

    public MessageNoticeVO(Long noticeId, String title, String content, Date createAt,  Long senderId, String username, String headPortrait, Integer status) {
        this.noticeId = noticeId;
        this.title = title;
        this.content = content;
        this.createAt = createAt;
        this.senderId = senderId;
        this.username = username;
        this.headPortrait = headPortrait;
        this.status = status;
    }

}
