package weilai.team.officialWebSiteApi.entity.admin.VO;

import lombok.Data;

/**
 * ClassName:searchUser
 * Description:
 *
 * @Author:独酌
 * @Create:2024/11/27 16:56
 */
@Data
public class SearchUserVO {

    private Long userId;

    private String name;

    private String userDestination;

    private String headPortrait;

    private Long postCount;

    private Long viewCount;
}
