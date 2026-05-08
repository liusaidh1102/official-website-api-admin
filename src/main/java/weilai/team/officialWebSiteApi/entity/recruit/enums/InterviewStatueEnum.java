package weilai.team.officialWebSiteApi.entity.recruit.enums;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lzw
 * @date 2024/11/12 21:20
 * @description 作用：面试的状态
 */
@Getter
public enum InterviewStatueEnum {

    /*
    数据库里的状态
     */
    WAIT_ARRANGE(0,"待安排"),
    REGISTER_FAIL(1,"报名失败"),
    WAIT_INTERVIEW(2,"待面试"),
    INTERVIEW_PASS(3,"已录取"),
    WAIT_SECOND_INTERVIEW(4,"待二面"),
    ELIMINATED(5,"未录取"),
    WAIT_FEEDBACK(6,"待反馈");


    private final Integer code;
    private final String message;

    InterviewStatueEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
