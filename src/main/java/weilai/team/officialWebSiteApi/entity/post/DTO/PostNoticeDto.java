package weilai.team.officialWebSiteApi.entity.post.DTO;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class PostNoticeDto {

    private Long noticeId;

    private  String title;

    private String content;

    private Long userId;

    private Date createAt;



    public PostNoticeDto() {
    }

    public PostNoticeDto(Long noticeId, String title, String content, Long userId, Date createAt) {
        this.noticeId = noticeId;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.createAt = createAt;
    }

    public PostNoticeDto(Long noticeId, String title, String content, Long userId) {
        this.noticeId = noticeId;
        this.title = title;
        this.content = content;
        this.userId = userId;
    }

}
