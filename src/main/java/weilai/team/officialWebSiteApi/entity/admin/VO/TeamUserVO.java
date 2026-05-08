package weilai.team.officialWebSiteApi.entity.admin.VO;

import lombok.Data;
import weilai.team.officialWebSiteApi.entity.admin.DO.User;

import java.util.List;

/**
 * ClassName:TeamUserVO
 * Description:
 *
 * @Author:独酌
 * @Create:2024/12/4 11:36
 */
@Data
public class TeamUserVO {
    private Long id;

    private String name;

    private String group;

    private String grade;

    private String studyId;

    private String clazz;

    private Long ladleUserId;

    private String ladleName;

    private String ladleGrade;

    private List<Integer> allGroup;

    public TeamUserVO(Long id, String name, String group,
                      String grade, String studyId, String clazz,
                      Long ladleUserId, String ladleName,
                      String ladleGrade, List<Integer> allGroup) {
        this.id = id;
        this.name = name;
        this.group = group;
        this.grade = grade;
        this.studyId = studyId;
        this.clazz = clazz;
        this.ladleUserId = ladleUserId;
        this.ladleName = ladleName;
        this.ladleGrade = ladleGrade;
        this.allGroup = allGroup;
    }

    public TeamUserVO(User user, User ladeleUser, List<Integer> allGroup){
        this.id = user.getId();
        this.studyId = user.getStudyId();
        this.name = user.getName();
        this.grade = user.getGrade();
        this.group = user.getGroup();
        this.clazz = user.getClazz();
        if(ladeleUser != null){
            this.ladleUserId = ladeleUser.getId();
            this.ladleName = ladeleUser.getName();
            this.ladleGrade = ladeleUser.getGrade();
        }
        this.allGroup = allGroup;
    }
}
