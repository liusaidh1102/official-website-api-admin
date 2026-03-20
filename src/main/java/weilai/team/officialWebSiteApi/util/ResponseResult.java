package weilai.team.officialWebSiteApi.util;

import lombok.Getter;

@Getter
public class ResponseResult<T>{


    private final Integer code; //状态码

    private final String message;  //信息

    private final T data; //数据

    /*
    Http 状态码
     */
    public static final ResponseResult<?> REQUEST_LIMIT = new ResponseResult<>(-1,"请不要频繁发起请求！","");
    public static final ResponseResult<?> OK = new ResponseResult<>(200,"请求成功","");
    public static final ResponseResult<?> Bad_Request = new ResponseResult<>(400,"请求无效或参数错误","");
    public static final ResponseResult<?> Unauthorized = new ResponseResult<>(401,"请检查token的完整性","");
    public static final ResponseResult<?> FORBIDDEN = new ResponseResult<>(403,"禁止访问","");
    public static final ResponseResult<?> NOT_FOUND = new ResponseResult<>(404,"资源未找到","");
    public static final ResponseResult<?> SERVICE_ERROR = new ResponseResult<>(500,"服务器内部错误","");
    public static final ResponseResult<?> SERVICE_BUSY = new ResponseResult<>(429,"服务器繁忙，请稍后重试","");

    /*
    登录修改密码逻辑状态码
     */
    public static final ResponseResult<?> LOGIN_SUCCESS = new ResponseResult<>(1000,"登录成功","");
    public static final ResponseResult<?> LOGIN_FILE = new ResponseResult<>(1001,"用户名或密码错误","");
    public static final ResponseResult<?> LOGIN_FAIL_USER_NOT_EXIST = new ResponseResult<>(1002,"用户不存在","");
    public static final ResponseResult<?> LOGIN_FAIL_NOT_ONE = new ResponseResult<>(1003,"账号已在别处登录。请重新登录","");
    public static final ResponseResult<?> EMAIL_FORMAT_ERROR = new ResponseResult<>(1004,"邮箱格式错误","");
    public static final ResponseResult<?> EMAIL_CAPTCHA_FOUND = new ResponseResult<>(1005,"验证码未过期","");
    public static final ResponseResult<?> EMAIL_CAPTCHA_NOT_FOUND = new ResponseResult<>(1006,"验证码已过期","");
    public static final ResponseResult<?> EMAIL_CAPTCHA_SEND_SUCCESS = new ResponseResult<>(1007,"验证码发送成功","");
    public static final ResponseResult<?> EMAIL_CAPTCHA_FILE = new ResponseResult<>(1008,"验证码错误","");
    public static final ResponseResult<?> UPDATE_PASSWORD_SUCCESS = new ResponseResult<>(1009,"密码修改成功","");

    /*
    评论状态码
     */
    public static final ResponseResult<?> COMMENT_NOT_FOUND = new ResponseResult<>(1010,"评论未找到","");

    /*
    个人中心状态码
     */
    public static final ResponseResult<?> USER_NOT_FOUND_COURSE = new ResponseResult<>(6000,"很遗憾，未添加课程表","");
    public static final ResponseResult<?> USER_NOT_FOUND_POST = new ResponseResult<>(6001,"很遗憾，未发布贴子","");
    public static final ResponseResult<?> USER_NOT_FOUND_COLLECT = new ResponseResult<>(6002,"很遗憾，没有任何收藏","");
    public static final ResponseResult<?> USER_SET_COURSE_FILE = new ResponseResult<>(6003,"很遗憾，添加失败","");
    public static final ResponseResult<?> USER_DELETE_COURSE_FILE = new ResponseResult<>(6004,"很遗憾，删除失败","");
    public static final ResponseResult<?> USER_UPDATE_COURSE_FILE = new ResponseResult<>(6005,"很遗憾，修改失败","");
    public static final ResponseResult<?> USER_NOT_FOUND = new ResponseResult<>(6006,"用户不存在","");
    public static final ResponseResult<?> USER_AUTHORITY_ERROR = new ResponseResult<>(6007,"权限不存在","");

    /*
    通讯录管理
     */
    public static final ResponseResult<?> GROUP_LADLE_EXIT = new ResponseResult<>(6020,"组长已经存在，不可再次添加","");
    public static final ResponseResult<?> GROUP_USER_EXIT = new ResponseResult<>(6021,"学号或邮箱已经存在","");

    /*
      帖子状态码
     */
    public static final ResponseResult<?> POST_PUT_SUCCESS =  new ResponseResult<>(2000,"发表成功","");
    public static final ResponseResult<?> POST_PUT_FAIL =  new ResponseResult<>(2001,"发表失败","");
    public static final ResponseResult<?> POST_DELETE_SUCCESS =  new ResponseResult<>(2002,"删除成功","");
    public static final ResponseResult<?> POST_DELETE_FAIL =  new ResponseResult<>(2003,"删除失败","");
    public static final ResponseResult<?> POST_ID_ISNULL= new ResponseResult<>(2004,"该帖子不存在","");
    public static final ResponseResult<?> UPDATE_FAIL = new ResponseResult<>(2005,"修改失败","");
    public static final ResponseResult<?> UPDATE_SUCCESS = new ResponseResult<>(2006,"修改成功","");
    public static final ResponseResult<?> GET_POST_SUCCESS = new ResponseResult<>(2007,"查询成功","");
    public static final ResponseResult<?> GET_POST_FAIL= new ResponseResult<>(2008,"查询失败","");
    public static final ResponseResult<?> POST_LIKE_SUCCESS = new ResponseResult<>(2009,"点赞成功","");
    public static final ResponseResult<?> POST_LIKE_FAIL = new ResponseResult<>(2010,"点赞失败","");
    public static final ResponseResult<?> UN_LIKE_SUCCESS = new ResponseResult<>(2011,"取消点赞成功","");
    public static final ResponseResult<?> UN_LIKE_FAIL = new ResponseResult<>(2012,"取消点赞失败","");
    public static final ResponseResult<?> COLLECTION_SUCCESS = new ResponseResult<>(2013,"收藏成功","");
    public static final ResponseResult<?> COLLECTION_FAIL = new ResponseResult<>(2014,"收藏失败","");
    public static final ResponseResult<?> COLLECTION_IS_EXIST = new ResponseResult<>(2015,"已收藏","");
    public static final ResponseResult<?> DELETE_COLLECTION_SUCCESS = new ResponseResult<>(2016,"删除收藏成功","");
    public static final ResponseResult<?> DELETE_COLLECTION_FAIL = new ResponseResult<>(2017,"删除收藏失败","");
    public static final ResponseResult<?> LIKED_COLLECTION=new ResponseResult<>(2018,"您已点赞","");
    public static final ResponseResult<?> IMAGE_OUT_LIMIT=new ResponseResult<>(2019,"图片数量超出限制","");
    public static final ResponseResult<?> NO_PERMISSION_POST=new ResponseResult<>(2020,"您没有发布公告的权限","");
    public static final ResponseResult<?> TYPE_NOT_EXIST = new ResponseResult<>(2021,"类型错误","");
    public static final ResponseResult<?> OPERATE_TOO_FREQUENT = new ResponseResult<>(2022,"操作过于频繁","");
    public static final ResponseResult<?> SYSTEM_BUSY = new ResponseResult<>(2050,"系统繁忙，请重试","");



    /**
     * 消息状态码
     */
    public static final ResponseResult<?> NO_PERMISSION=new ResponseResult<>(4000,"没有权限","");
    public static final ResponseResult<?> SSE_CONNECTION_SUCCESS=new ResponseResult<>(4001,"sse连接成功","");
    public static final ResponseResult<?> FAILED_TO_SEND=new ResponseResult<>(4002,"发送失败","");
    public static final ResponseResult<?> NOT_FOUND_MESSAGE=new ResponseResult<>(4003,"未查找到信息","");
    /**
     * 标签功能状态码
     */
    public static final ResponseResult<?> ADD_SUCCESS=new ResponseResult<>(5000,"帖子标签添加成功","");
    public static final ResponseResult<?> ADD_FAILURE=new ResponseResult<>(5003,"帖子标签添加失败，已添加过该标签","");
    public static final ResponseResult<?> ADD_FAIL = new ResponseResult<>(5005,"添加的标签不能超过7个","");
    public static final ResponseResult<?> DELETE_SUCCESS =new ResponseResult<>(5006,"帖子标签删除成功","");
    public static final ResponseResult<?> DELETE_FAILURE =new ResponseResult<>(5008,"帖子标签删除失败","");
    public static final ResponseResult<?> TAG_NOT_FOUND =new ResponseResult<>(5009,"未找到需要删除的标签","");
    public static final ResponseResult<?>PARAM_ERROR =new ResponseResult<>(50010,"传入的参数无效或不符合预期","");
    public static final ResponseResult<?>RECOMMEND_SUCCESS =new ResponseResult<>(50011,"标签推荐成功","");
    public static final ResponseResult<?> HOT_SUCCESS = new ResponseResult<>(50012,"热门标签展示成功","");
    public static ResponseResult<?> UseNotFound=new ResponseResult<>(50021,"未找到类型的使用标签的信息","");
    public static final ResponseResult<?> USAGE_STATISTICS = new ResponseResult<>(50013,"标签使用次数统计","");
    public static final ResponseResult<?> GET_ALL_TAG_NAMES = new ResponseResult<>(50014,"获得所有标签","");
    public static final ResponseResult<?> ERROR = new ResponseResult<>(50015,"标签获取失败","");
    public static final ResponseResult<?> FIND_POST =new ResponseResult<>(50016,"查找相关帖子","") ;
    public static final ResponseResult<?> FAIL_FIND_POST = new ResponseResult<>(50017,"未找到相关帖子信息","");
    public static final ResponseResult<?> LABILE_FILTER = new ResponseResult<>(50018,"模糊匹配成功","");
    public static final ResponseResult<?> LABILE_FILE = new ResponseResult<>(50019,"无法检索标签名称","");



    /*
    3000以上的状态码
     */
    public static final ResponseResult<?> FILE_IS_NULL = new ResponseResult<>(3001,"文件不能为空","");
    public static final ResponseResult<?> PARAM_IS_NOT_VALID = new ResponseResult<>(3002,"请求参数不合法","");
    public static final ResponseResult<?> INSERT_ERROR = new ResponseResult<>(3003,"数据插入失败，请检查数据是否重复插入","");



    /*
    3100以上的状态码 : 招新报名的返回验证
     */
    public static final ResponseResult<?> STUDENT_ID_IS_NOT_VALID = new ResponseResult<>(3100,"学号应该为11位数字","");
    public static final ResponseResult<?> SEX_IS_NOT_VALID = new ResponseResult<>(3101,"性别只能是男或女","");
    public static final ResponseResult<?> ALREADY_REGISTER = new ResponseResult<>(3102,"已经报过名了","");
    public static final ResponseResult<?> FILE_TYPE_ERROR = new ResponseResult<>(3103,"文件的类型不匹配,或文件大小为空","");
    public static final ResponseResult<?> FILE_SIZE_ERROR = new ResponseResult<>(3104,"文件大小不符","");
    public static final ResponseResult<?> QQ_NUMBER_IS_NOT_VALID = new ResponseResult<>(3105,"qq号不合法","");
//    public static final ResponseResult<?> FILE_UPLOAD_FAIL = new ResponseResult<>(3105,"文件上传失败，请联系管理员处理","");
//    public static final ResponseResult<?> REGISTER_FAIL = new ResponseResult<>(3106,"报名失败，请联系管理员解决","");



    /*
    3200以上的状态码:招新名单管理的返回验证
     */
    public static final ResponseResult<?> TIME_FORMAT_ERROR = new ResponseResult<>(3200,"筛选的时间格式不对","");
    public static final ResponseResult<?> INTERVIEW_STATUS_ERROR = new ResponseResult<>(3201,"报名状态修改不合法","");
    public static final ResponseResult<?> INTERVIEW_ID_NOT_BE_NULL = new ResponseResult<>(3202,"修改人的id不能为空","");
    public static final ResponseResult<?> NOT_FOUND_RESOURCE = new ResponseResult<>(3203,"未找到该用户的简历","");
    public static final ResponseResult<?> PARAM_ILLEGAL = new ResponseResult<>(3204,"查询参数不对","");
    public static final ResponseResult<?> ERROR_RESULT_EXPORT = new ResponseResult<>(3205,"结果导出失败","");
    public static final ResponseResult<?> RESULT_IS_NULL = new ResponseResult<>(3206,"暂无符合该状态的结果","");
    public static final ResponseResult<?> ID_IS_NOT_EXIST = new ResponseResult<>(3207,"用户的id不存在","");

    /*
    3300以上的状态码:招新面试的返回验证
     */
    public static final ResponseResult<?> PARAM_ILLEGAL_INTERVIEW = new ResponseResult<>(3300,"面评参数不合法","");
    public static final ResponseResult<?> INTERVIEW_PARAM_ILLEGAL = new ResponseResult<>(3302,"查询的参数不合法","");
    public static final ResponseResult<?> STATUS_NOT_FOUND = new ResponseResult<>(3303,"修改的状态不存在","");
    public static final ResponseResult<?> ROUND_NOT_EXIST = new ResponseResult<>(3304,"只能是一面或二面","");
    public static final ResponseResult<?> INTERVIEW_NOT_FOUND = new ResponseResult<>(3305,"面试记录不存在或面评为空","");

    /*
    3400以上的状态码:文件上传的  0成功，1失败
     */
    public static final ResponseResult<?> FILE_FOUND = new ResponseResult<>(3400,"文件存在，无需上传","");
    public static final ResponseResult<?> FILE_UPLOADING = new ResponseResult<>(3401,"文件正在上传","");
    public static final ResponseResult<?> FILE_MERGER_SUCCESS = new ResponseResult<>(3402,"文件合并成功","");
    public static final ResponseResult<?> FILE_INIT_SUCCESS = new ResponseResult<>(3403,"分片上传初始化成功","");
    public static final ResponseResult<?> FILE_IS_SINGLE = new ResponseResult<>(3404,"单文件上传","");


    public static final ResponseResult<?> FILE_NOT_FOUND = new ResponseResult<>(3410,"文件不存在，请上传","");
    public static final ResponseResult<?> FILE_MERGER_ERROR = new ResponseResult<>(3411,"文件合并失败","");
    public static final ResponseResult<?> FILE_INIT_FAIL = new ResponseResult<>(3412,"分片上传初始化失败","");
    public static final ResponseResult<?> FILE_NOT_INIT = new ResponseResult<>(3413,"文件还未初始化","");
    public static final ResponseResult<?> FILE_DATA_ERROR = new ResponseResult<>(3414,"excel文件表头不合法或内容包含空字段","");
    public static final ResponseResult<?> ERROR_INSERT = new ResponseResult<>(3415,"数据插入失败，请确保索引的唯一性","");
    public static final ResponseResult<?> FILE_LOAD_ERROR = new ResponseResult<>(3416,"文件读取失败","");

    /*钉钉状态码*/
    public static final ResponseResult<?> DING_ERROR_DING_LONG = new ResponseResult<>(7000,"钉钉accessToken获取失败","");






    private ResponseResult(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public <K> ResponseResult<K> put(K data) {
        return new ResponseResult<>(this.code,this.message,data);
    }
}
