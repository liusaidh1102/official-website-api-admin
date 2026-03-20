package weilai.team.officialWebSiteApi.entity.admin.VO;

import lombok.Data;
import weilai.team.officialWebSiteApi.entity.admin.DO.User;

import java.util.List;

/**
 * ClassName:TeamUserList
 * Description:
 *
 * @Author:独酌
 * @Create:2024/12/2 19:00
 */
@Data
public class TeamUserListVO {

    private Long id;

    private String name;

    private String group;

    private String grade;

    private String studyId;

    private String clazz;

    private Boolean isLeader;

    public TeamUserListVO(User user,Boolean isLeader){
        this.id = user.getId();
        this.studyId = user.getStudyId();
        this.name = user.getName();
        this.grade = user.getGrade();
        this.group = user.getGroup();
        this.clazz = user.getClazz();
        this.isLeader = isLeader;
    }
}
