package weilai.team.officialWebSiteApi.util;

public final class Values {
    /**
     * # JWT的基础信息
     *   key: 安全密钥
     *   JWTKey: 加密密钥
     *   outTime:设置每个账号的超时时间
     *   tokenParamName: 'token' # 用户令牌验证的参数名
     */
//    public static final String KEY = "dx5frb8yn67klop2dzqpmdiencsurv5*";
    public static final String KEY = "dxf867lodqdisr5*";
    public static final String JWT_KEY = "81e78eac24ab4a2d8eb28fd8038eaf42";
    public static final Long OUT_TIME = 7200000L; //token的过期时间120分钟(redis 和 jwt 的过期时间一致)
    public static final String TOKEN_PARAM_NAME = "Authorization"; //获取token的参数名
    public static final String TOKEN_PREFIX = "Bearer"; //获取token的参数名
    public static final String REDIS_TOKEN_PREFIX = "token: "; // 存储在redis中的token的前缀

    // 存储在redis中的token的唯一标识，保证同一时间，只能有一个账号登录
    public static final String REDIS_TOKEN_ID = "token_id: ";

    //邮箱格式
    public static final String EMAIL_FORMAT = "^\\w{3,}(\\.\\w+)*@[A-z0-9]+(\\.[A-z]{2,5}){1,2}$";
    //学号验证（11位的学号）
    public static final String STUDENT_ID_FORMAT = "^\\d{11}$";
    //性别验证，男或女
    public static final String SEX_FORMAT = "^(男|女)$";
    //qq号验证
    public static final String QQ_NUMBER_FORMAT = "^[0-9]{5,10}$";

    //邮箱验证码前缀
    public static final String REDIS_CAPTCHA_PREFIX = "captcha: ";

    //点赞收藏信息过期时间 60*60*24*7*1000
    public static final Long MESSAGE_OUT_TIME=604800000L;

    //redis存储评论信息前缀
    public static final String REDIS_COMMENT_PREFIX="comment: ";

    //redis存储未读评论数量
    public static  final String REDIS_NOT_READ_COMMENT_COUNT_PREFIX="not_read_comment_count: ";

    //redis存储所有评论数量
    public static  final String REDIS_ALL_COMMENT_COUNT_PREFIX="all_comment_count: ";

    //redis存储点赞和收藏信息前缀
    public static final String REDIS_LIKE_OR_COLLECT_PREFIX="like_or_collect: ";

    //redis存储未读点赞或收藏数量
    public static  final String REDIS_NOT_READ_LIKE_OR_COLLECT_COUNT_PREFIX="not_read_like_or_collect_count: ";

    //redis存储未读点赞或收藏数量
    public static  final String REDIS_ALL_LIKE_OR_COLLECT_COUNT_PREFIX="all_like_or_collect_count: ";

    //redis存储系统通知前缀
    public static final String REDIS_SYSTEM_NOTIFICATION_PREFIX="system_notification: ";

    //redis存储未读点赞或收藏数量
    public static  final String REDIS_NOT_READ_SYSTEM_NOTIFICATION_COUNT_PREFIX="not_read_system_notification_count: ";

    //redis存储未读点赞或收藏数量
    public static  final String REDIS_ALL_SYSTEM_NOTIFICATION_COUNT_PREFIX="all_system_notification_count: ";

    //redis存储公告信息
    public static  final String REDIS_ALL_NOTICE="all_notice";

    //redis中公告过期时间 60*60*24*365*1000 一年
    public static final Long NOTICE_OUT_TIME=31536000000L;

    //redis存储公告前缀
    public static  final String REDIS_NOTICE_USER_PREFIX="notice_user: ";

    //redis存储公告数量
    public static  final String REDIS_NOTICE_COUNT="notice_count: ";


    //个人未读公告数量
    public static  final String REDIS_NO_READ_NOTICE_COUNT_PREFIX="no_read_notice_count: ";

    /*
     * 招新报名的验证码前缀
     */
    public static final String REDIS_RECRUIT_CAPTCHA_PREFIX = "recruit: ";
    /*
     * 招新报名的验证码过期时间（毫秒） 5分钟过时
     */
    public static Long REDIS_RECRUIT_EXPIRE_TIME = 5 * 60 * 1000L;
    /*
     * 报名文件最大大小（1MB）
     */
    public static final Long RECRUIT_FILE_MAX_SIZE = 1024 * 1024 * 20L;


    //帖子
    public static final Long POST_OUT_TIME=60*60*1000L; //1小时后过期
    public static final Long PAGE_TTL=24*60*60*1000L;//分页缓存时间为一天
    public static final String KEY_POST_VO="postVo:";
    public static final String COLLECT_COUNT_KEY="post:collectCount:";
    public static final String KEY_LIKED="post:isLike:";
    public static final String POST_CREATE_LOCK="post_lock";
    public static final String PAGE_KEY="page:";
    public static final String POST_TAGS_KEY="postTags:";
    public static final String IS_COLLECT__KEY="post:isCollect:";
    public static final String COMMENT_COUNT_KEY="commentCount:";
    public static final String POST_VIEW_COUNT_KEY="view_count:";
    public static final String POST_PAGE_LOCK="page_lock:";
    public static final Long NULL_CACHE_EXPIRE_TIME=1000*5L;
    public static final String LIKE_COUNT="like:count:";
    public static final String POST_LIKE_LOCK="like_lock: ";
    //用户
    public static final String USER_INFO_PREFIX="userInfo: ";  //用户的个人信息前缀
    public static final long USER_INFO_OUT_TIME = 1L; // 1小时后过期(单位为小时)

    public static final String USER_COURSE_PREFIX="userCourse: "; //用户课程信息前缀
    public static final long USER_COURSE_OUT_TIME = 1L; // 1小时后过期(单位为小时)

    public static final String USER_POST_PREFIX="userPost: "; //用户个人中心的帖子前缀
    public static final long USER_POST_OUT_TIME = 1L; // 1小时后过期(单位为小时)

    public static final String USER_COLLECT_PREFIX="userCollect: "; //用户个人中心的收藏前缀
    public static final long USER_COLLECT_OUT_TIME = 1L; // 1小时后过期(单位为小时)

    public static final String LIKE_COMMENTS_PREFIX = "like_comment: "; //用户个人中心的收藏前缀
    public static final String USER_AUTHORITY_PREFIX = "userAuthority"; //查询用户权限列表缓存
    public static final long USER_AUTHORITY_OUT_TIME = 1L; // 1小时后过期(单位为小时)

    public static final String TEAM_GROUP_PREFIX = "teamGroup: "; //查询通讯录的用户缓存
    public static final long TEAM_GROUP_OUT_TIME = 1L; // 1小时后过期(单位为小时)
    public static final String TEAM_USER_PREFIX = "teamUser: "; //查询通讯录的用户缓存
    public static final long TEAM_USER_OUT_TIME = 5L; // 5 分钟后过期



    public static final String TEAM_SEARCH_PREFIX="teamSearch"; //模糊查询团队用户
    public static final long TEAM_SEARCH_OUT_TIME = 1L; // 1小时后过期(单位为小时)

    public static final String USER_LEFT_PHOTO = "user_life_photo: ";
    public static final long USER_LEFT_PHOTO_OUT_TIME = 1L; // 1小时后过期(单位为小时)

    public static final String DEFAULT_PASSWORD = "$2a$10$diEHiLoxOssiYcYdvbb0XOwc2fmy0y.WRx2IH/rlcTxnutI1kqMLe"; //默认密码

    public static final String ACCESS_TOKEN="accessToken";
    public static final long ACCESS_TOKEN_OUT_TIME = 2L; // 1小时后过期(单位为小时)

    public static final String ATTENDANCE_INFO_SINGLE = "attendanceInfoSingle";
    public static final long ATTENDANCE_INFO_SINGLE_OUT_TIME = 1L; // 小时

    public static final String ATTENDANCE_INFO_SPAN = "attendanceInfoSpan";
    public static final long ATTENDANCE_INFO_SPAN_OUT_TIME = 1L; // 天

    // 考勤名单类型
    // 应到人名单
    public static final String USER_SHOULD_NAMES = "userShouldName";
    // 实到人名单
    public static final String USER_CURRENT_NAMES = "userCurrentName";
    // 请假人名单
    public static final String USER_LEAVES_NAMES = "userLeavesName";
    // 迟到人名单
    public static final String USER_LATE_NAMES = "userLateName";

    // 权限缓存
    public static final String USER_PERMISSION = "user_permission";
    // 1 小时
    public static final long USER_PERMISSION_OUT_TIME = 1L;

    // 限流
    public static final String LIMITING_PREFIX = "CURRENT_LIMITING: ";
}
