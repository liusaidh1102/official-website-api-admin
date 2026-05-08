package weilai.team.officialWebSiteApi.entity.admin.VO;

import com.alibaba.fastjson2.JSON;
import lombok.Data;
import weilai.team.officialWebSiteApi.entity.admin.DO.UserCourse;
import weilai.team.officialWebSiteApi.entity.admin.DTO.UserCourseBO;

import java.util.*;

/**
 * ClassName:UserCourseVO
 * Description:
 *
 * @Author:独酌
 * @Create:2024/11/19 20:44
 */
@Data
public class UserCourseVO{

    //用于映射课程的时间，将 1-2 映射到 0，3-4 映射到 1，以此类推
    private static final Map<String,Integer> courseTime;

    //用于创建一个空的用户课程表，用于填充空的课程表，保证发给前端的课程表是完整的
    //即，每天 5节课
    private static final List<UserCourseBO> userCourse;

    private List<List<UserCourseBO>> allCourse;

    static {
        courseTime = new HashMap<>();
        userCourse = new ArrayList<>();
        for(int i = 0;i < 5;i++){
            courseTime.put("1-2",0);
            courseTime.put("3-4",1);
            courseTime.put("5-6",2);
            courseTime.put("7-8",3);
            courseTime.put("9-10",4);
            userCourse.add(null);
        }
    }

    public UserCourseVO(UserCourse userCourse){
        if(userCourse != null){
            allCourse = new ArrayList<>();
            allCourse.add(setUserCoursePosition(userCourse.getMonday()));
            allCourse.add(setUserCoursePosition(userCourse.getTuesday()));
            allCourse.add(setUserCoursePosition(userCourse.getWednesday()));
            allCourse.add(setUserCoursePosition(userCourse.getThursday()));
            allCourse.add(setUserCoursePosition(userCourse.getFriday()));
            allCourse.add(setUserCoursePosition(userCourse.getSaturday()));
            allCourse.add(setUserCoursePosition(userCourse.getSunday()));
        } else {
            allCourse = new ArrayList<>();
            allCourse.add(new ArrayList<>(Arrays.asList(null,null,null,null,null)));
            allCourse.add(new ArrayList<>(Arrays.asList(null,null,null,null,null)));
            allCourse.add(new ArrayList<>(Arrays.asList(null,null,null,null,null)));
            allCourse.add(new ArrayList<>(Arrays.asList(null,null,null,null,null)));
            allCourse.add(new ArrayList<>(Arrays.asList(null,null,null,null,null)));
            allCourse.add(new ArrayList<>(Arrays.asList(null,null,null,null,null)));
            allCourse.add(new ArrayList<>(Arrays.asList(null,null,null,null,null)));
        }
    }

    private List<UserCourseBO> setUserCoursePosition(String course){
        List<UserCourseBO> userCourseBOS = JSON.parseArray(course, UserCourseBO.class);
        List<UserCourseBO> userCourseBOSTemp = new ArrayList<>();

        boolean isCopySuccess = userCourseBOSTemp.addAll(userCourse);

        if(!isCopySuccess)
            for (int i = 0; i < 5; i++) {
                userCourseBOSTemp.add(null);
                userCourse.add(null);
            }

        if(userCourseBOS != null)
            for(UserCourseBO u : userCourseBOS) {
                userCourseBOSTemp.set(courseTime.get(u.getCourseTime()), u);
            }

        return userCourseBOSTemp;
    }
}
