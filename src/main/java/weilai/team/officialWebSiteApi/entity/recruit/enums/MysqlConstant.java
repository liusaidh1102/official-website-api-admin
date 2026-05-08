package weilai.team.officialWebSiteApi.entity.recruit.enums;

/**
 * @author lzw
 * @date 2024/12/3 21:44
 * @description 作用：mysql数据据常量
 */
public class MysqlConstant {

    /*
    * 用户是否被删除，0未删除，1已删除
     */
    public static final Integer IS_NOT_DELETED = 0;
    public static final Integer IS_DELETED = 1;


    /*
    * 招新报名用户的状态，0代表待安排；1代表待面试；2代表已录取；3代表未录取
     */
    public static final Integer STATUS_WAIT_ARRANGE = 0;
    public static final Integer STATUS_WAIT_INTERVIEW = 1;
    public static final Integer STATUS_ADMIT = 2;
    public static final Integer STATUS_NOT_ADMIT = 3;


    /*
    面试的状态：-1查询全部，0查询待我面试，1待反馈；2已录取；3未录取;4待二面）
     */
    public static final Integer ALL_INTERVIEW = -1;
    public static final Integer WAIT_MY_INTERVIEW = 0;
    public static final Integer WAIT_FEEDBACK = 1;
    public static final Integer ADMIT = 2;
    public static final Integer NOT_ADMIT = 3;

    /*
    是不是面试官
     */
    public static final Integer IS_NOT_HR = 0;
    public static final Integer IS_HR = 1;



}
