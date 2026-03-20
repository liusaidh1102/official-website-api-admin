package weilai.team.officialWebSiteApi.entity.admin.VO;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

/**
 * ClassName:TeamAbleVO
 * Description:
 *
 * @Author:独酌
 * @Create:2024/12/2 18:42
 */
@Data
public class TeamAbleVO {

    private Integer grade;

    private List<String> group;

    public void setGroup (String group){
        this.group = Arrays.asList(group.split(","));
    }
}
