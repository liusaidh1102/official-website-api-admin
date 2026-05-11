package weilai.team.officialWebSiteApi.entity.admin.VO;

import lombok.Data;

import java.util.List;

/**
 * ClassName:TokenVO
 * Description:
 *
 * @Author:独酌
 * @Create:2024/11/11 22:53
 */
@Data
public class TokenVO {

    private String accessToken;

//    private String refreshToken;

    private Long userId;

    private List<String> permissions;

    public TokenVO(String accessToken,Long userId,List<String> permissions) {
        this.userId = userId;
        this.accessToken = accessToken;
//        this.refreshToken = refreshToken;
        this.permissions = permissions;
    }
}
