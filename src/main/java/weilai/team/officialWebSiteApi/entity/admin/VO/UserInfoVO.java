package weilai.team.officialWebSiteApi.entity.admin.VO;

import com.alibaba.fastjson2.JSON;
import lombok.Data;
import weilai.team.officialWebSiteApi.entity.admin.DO.User;
import weilai.team.officialWebSiteApi.entity.admin.DO.UserCourse;

import java.util.Date;
import java.util.List;

/**
 * ClassName:UserInfoVO
 * Description:
 *
 * @Author:独酌
 * @Create:2024/11/12 20:21
 */
@Data
public class UserInfoVO {

    public UserInfoVO(User user) {
        userId = user.getId();
        email = user.getEmail();
        name = user.getName();
        sex = user.getSex();
        clazz = user.getClazz();
        grade = user.getGrade();
        group = user.getGroup();
        direction = user.getDirection();
        studyId = user.getStudyId();
        headPortrait = user.getHeadPortrait();
        phone = user.getPhone();
        qq = user.getQq();
        graduationDestination = user.getGraduationDestination();
        userDestination = user.getUserDestination();
        lifePhoto = JSON.parseArray(user.getLifePhoto(), String.class);
        lastLoginTime = user.getLastLoginTime();
        lanQiaoCount = user.getLanQiaoCount();
        copyrightCount = user.getCopyrightCount();
        salaryYear = user.getSalaryYear();
        area = user.getArea();
        auth = user.getAuth();
    }
    private Long userId;
    private String email;
    private String name;
    private String sex;
    private String clazz;
    private String grade;
    private String group;
    private String direction;
    private String studyId;
    private String headPortrait;
    private String phone;
    private String qq;
    private String graduationDestination;
    private String userDestination;
    private List<String> lifePhoto;
    private Date lastLoginTime;
    private Integer lanQiaoCount;
    private Integer copyrightCount;
    private Integer salaryYear;
    private String area;
    private List<String> auth;
}
