package weilai.team.officialWebSiteApi.entity.admin.DTO;

import lombok.Data;

/**
 *ClassName:DingIDDTO
 *Description:
 *@Author:独酌
 *@Create:2025/3/12 16:57
 */
@Data
public class DingIDDTO {

    /**
     *  钉钉的用户id
     */
    private String dingID;

    /**
     *  用户名
     */
    private String name;

    /**
     * 用户所属的组别
     */
    private String group;

    /**
     * 用户id
     */
    private Long userId;
}
