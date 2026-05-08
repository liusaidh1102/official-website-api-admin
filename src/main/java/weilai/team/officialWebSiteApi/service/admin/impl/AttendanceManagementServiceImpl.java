package weilai.team.officialWebSiteApi.service.admin.impl;

import com.aliyun.dingtalkoauth2_1_0.Client;
import com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenRequest;
import com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenResponse;
import com.aliyun.teaopenapi.models.Config;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.*;
import com.dingtalk.api.response.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import weilai.team.officialWebSiteApi.entity.admin.DO.UserCourse;
import weilai.team.officialWebSiteApi.entity.admin.DTO.DingIDDTO;
import weilai.team.officialWebSiteApi.entity.admin.VO.CheckInfoSingleVO;
import weilai.team.officialWebSiteApi.entity.admin.VO.CheckInfoVO;
import weilai.team.officialWebSiteApi.mapper.admin.UserCourseMapper;
import weilai.team.officialWebSiteApi.mapper.admin.UserMapper;
import weilai.team.officialWebSiteApi.service.admin.AttendanceManagementService;
import weilai.team.officialWebSiteApi.util.RedisCacheUtil;
import weilai.team.officialWebSiteApi.util.RedisUtil;
import weilai.team.officialWebSiteApi.util.ResponseResult;
import weilai.team.officialWebSiteApi.util.Values;
import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import static weilai.team.officialWebSiteApi.util.Values.*;

/**
 * ClassName:AttendanceManagementServiceImpl
 * Description:
 *
 * @Author:独酌
 * @Create:2025/3/11 10:55
 */
@Service
@Slf4j
public class AttendanceManagementServiceImpl implements AttendanceManagementService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private RedisCacheUtil redisCacheUtil;

    @Resource
    private UserCourseMapper userCourseMapper;

    private static String accessToken;

    @Value("${ding.app_key}")
    private String DING_APP_KEY;

    @Value("${ding.app_secret}")
    private String DING_APP_SECRET;

    @Override
    public ResponseResult<?> getAttendanceInfoBySingleTime(Date time,String group,Integer shift){
        if(group == null || group.isEmpty()){
            return ResponseResult.Bad_Request.put("group 参数错误的接收的值为：" + group);
        }

        CheckInfoSingleVO checkInfoSingleVO = redisCacheUtil.queryWithPassThroughByHash(
                Values.ATTENDANCE_INFO_SINGLE,
                "",
                time + "::" + group + "::" + shift,
                CheckInfoSingleVO.class,
                ()->{
                    if(checkIsOutTime()) {
                        // 已经过期
                        String andSaveAccessToken = getAndSaveAccessToken();
                        if(andSaveAccessToken == null) {
                            return null;
                        }
                    }


                    long userCountShould; // 应到人数
                    int userCountCurrent; // 实到人数
                    int leavesUserCount; // 请假人数
                    int lateUserCount; // 迟到人数
                    Map<String,Integer[]> mapBiao = new HashMap<>();
                    Map<Date,Integer[]> mapDaw = new HashMap<>();
                    Map<String,List<String>> detail = new HashMap<>();


                    List<DingIDDTO> dingIDByGroup; // 存放的是钉钉的id列表
                    Map<String,String> tempMap = new HashMap<>(); // 存放的是钉钉的id和对应的组别之间的映射关系
                    Map<String,String> userIdName = new HashMap<>(); // 存放的是钉钉的id和用户名之间的映射关系
                    Map<String,Long> userIdsWithName = new HashMap<>(); // 存放姓名和用户id的映射
                    if("全部".equals(group)){
                        dingIDByGroup = userMapper.getDingIDAll();
                        userCountShould = userMapper.getUserCountByGroup(null);
                        ArrayList<String> userShouldList = new ArrayList<>();
                        if(dingIDByGroup != null) {
                            for(DingIDDTO d : dingIDByGroup) {
                                userShouldList.add(d.getName());
                                userIdName.put(d.getDingID(),d.getName());
                                userIdsWithName.put(d.getName(),d.getUserId());
                            }
                        }
                        detail.put(USER_SHOULD_NAMES,userShouldList);
                    } else {
                        dingIDByGroup = userMapper.getDingIDByGroup(Collections.singletonList(group));
                        userCountShould = userMapper.getUserCountByGroup(group);
                        ArrayList<String> userName = new ArrayList<>();
                        if(dingIDByGroup != null) {
                            for(DingIDDTO d : dingIDByGroup) {
                                userName.add(d.getName());
                                userIdName.put(d.getDingID(),d.getName());
                            }
                        }
                        detail.put(USER_SHOULD_NAMES,userName);
                    }

                    if(dingIDByGroup == null || dingIDByGroup.isEmpty())
                        return new CheckInfoSingleVO(0,0,0,0,
                                new HashMap<>(),new HashMap<>(),new HashMap<>());

                    List<String> dingUserIdList = new ArrayList<>();
                    for (DingIDDTO dingIDDTO : dingIDByGroup) {
                        dingUserIdList.add(dingIDDTO.getDingID());
                        mapBiao.computeIfAbsent(dingIDDTO.getGroup(), k -> new Integer[]{0, 0});
                        mapBiao.get(dingIDDTO.getGroup())[0] ++;
                        tempMap.put(dingIDDTO.getDingID(),dingIDDTO.getGroup());
                    }

                    StringBuilder sb = new StringBuilder();
                    for (String u : dingUserIdList) {
                        sb.append(u).append(",");
                    }
                    sb.deleteCharAt(sb.length() - 1);

                    Date[] dateAt00And24 = getDateByShift(time,shift);
                    Date dateAt00 = dateAt00And24[0];
                    Date dateAt24 = dateAt00And24[1];

                    Set<String> set = new HashSet<>();
                    List<String> lateUserList = new ArrayList<>();
                    long i = 1;
                    List<String> userCurrentList = new ArrayList<>();
                    while(true) { // 死循环，一直获取到没有数据为止
                        List<OapiAttendanceListResponse.Recordresult> checkInfo = getCheckInfo(dateAt00, dateAt24, dingUserIdList, i, 50L);
                        if(checkInfo != null) {
                            if(checkInfo.isEmpty()) {
                                break;
                            }
                            // 用于计算已经签到的人数
                            for (OapiAttendanceListResponse.Recordresult r : checkInfo) {
                                Date base = r.getBaseCheckTime();
                                if(base.after(dateAt00) && base.before(dateAt24)) {
                                    if(!set.contains(r.getUserId()) && "Normal".equals(r.getTimeResult()) && "Normal".equals(r.getLocationResult())) {
                                        String groupTemp = tempMap.get(r.getUserId());
                                        mapBiao.get(groupTemp)[1] ++;
                                        userCurrentList.add(userIdName.get(r.getUserId()));
                                        set.add(r.getUserId()); // 通过set去重机制，获取已经签到的人数
                                    } else if (!set.contains(r.getUserId()) && "Late".equals(r.getTimeResult())){
                                        Date baseCheckTime = r.getBaseCheckTime();
                                        Date userCheckTime = r.getUserCheckTime();
                                        // 计算相差多少分钟
                                        long minutes = (userCheckTime.getTime() - baseCheckTime.getTime()) / (1000 * 60);
                                        userCurrentList.add(userIdName.get(r.getUserId()) + "（迟到 " + minutes + " 分钟）");
                                        set.add(r.getUserId());
                                    }
                                }
                            }
                            i++;
                        } else {
                            return null;
                        }
                    }
                    detail.put(USER_CURRENT_NAMES,userCurrentList);

                    userCountCurrent = set.size();
                    // 获取当天请假的人数
                    leavesUserCount = getUserLeavesCount(sb.toString(), dateAt00, dateAt24,userIdName,detail);

                    // 创建Calendar实例并设置时间为给定日期
                    Calendar calendarTemp = Calendar.getInstance();
                    for(int j = -6;j < 0;j++) {
                        calendarTemp.setTime(time);
                        int userCountCurrentTemp = 0;
                        int userCountShouldTemp = (int) userCountShould;
                        // 向前推七天
                        calendarTemp.add(Calendar.DAY_OF_MONTH, j);
                        // 获取向前推七天后的日期
                        Date dateTemp = calendarTemp.getTime();
                        Date[] dateAt00And24Temp = getDateByShift(dateTemp,0);
                        Date dateAt00Temp = dateAt00And24Temp[0];
                        Date dateAt24Temp = dateAt00And24Temp[1];
                        set.clear();
                        i = 1;
                        while(true) {
                            List<OapiAttendanceListResponse.Recordresult> checkInfo = getCheckInfo(dateAt00Temp,dateAt24Temp, dingUserIdList, i, 50L);
                            if(checkInfo != null) {
                                if(checkInfo.isEmpty()) {
                                    break;
                                }
                                // 用于计算已经签到的人数
                                for (OapiAttendanceListResponse.Recordresult r : checkInfo) {
                                    if(("Normal".equals(r.getTimeResult()) && "Normal".equals(r.getLocationResult())) || "Late".equals(r.getTimeResult())) {
                                        set.add(r.getUserId()); // 通过set去重机制，获取已经签到的人数
                                    }
                                }
                                i++;
                            } else {
                                return null;
                            }
                        }
                        userCountCurrentTemp = set.size();
                        Integer[] tempAll = {userCountShouldTemp,userCountCurrentTemp};
                        mapDaw.put(dateAt00Temp,tempAll);
                    }
                    List<String> should = detail.get(USER_SHOULD_NAMES);
                    List<String> current = detail.get(USER_CURRENT_NAMES);
                    List<String> leaves = detail.get(USER_LEAVES_NAMES);
                    // 求 should 集合 - current 集合 -  leaves 集合
                    Date currentTime = new Date();
                    for (String s : should) {
                        if(!checkNameExist(current,s) && !checkNameExist(leaves,s) && !checkNameExist(lateUserList,s)) {
                            if(isSameDate(time,currentTime)) {
                                // 判断未打卡的人当前时间是否有课
                                String isCourse = "未知";
                                Long userId = userIdsWithName.get(s);
                                UserCourse userCourse = userCourseMapper.selectAllByUseId(userId);
                                if(userCourse != null) {
                                    // 根据时间判断当前是星期几
                                    LocalDate localDate = currentTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                    int dayOfWeek = localDate.getDayOfWeek().getValue();
                                    String timeSlot = getTimeSlot(currentTime);

                                    log.info(s + "::" + dayOfWeek + "::" + timeSlot + "::" + currentTime);

                                    switch (dayOfWeek) {
                                        case 1:
                                            String monday = userCourse.getMonday();
                                            if(monday != null && monday.contains(timeSlot)) isCourse = "⭐有课";
                                            else isCourse = "无课";
                                            break;
                                        case 2:
                                            String tuesday = userCourse.getTuesday();
                                            if(tuesday != null && tuesday.contains(timeSlot)) isCourse = "⭐有课";
                                            else isCourse = "无课";
                                            break;
                                        case 3:
                                            String wednesday = userCourse.getWednesday();
                                            if(wednesday != null && wednesday.contains(timeSlot)) isCourse = "⭐有课";
                                            else isCourse = "无课";
                                            break;
                                        case 4:
                                            String thursday = userCourse.getThursday();
                                            if(thursday != null && thursday.contains(timeSlot)) isCourse = "⭐有课";
                                            else isCourse = "无课";
                                            break;
                                        case 5:
                                            String friday = userCourse.getFriday();
                                            if(friday != null && friday.contains(timeSlot)) isCourse = "⭐有课";
                                            else isCourse = "无课";
                                            break;
                                        case 6:
                                            String saturday = userCourse.getSaturday();
                                            if(saturday != null && saturday.contains(timeSlot)) isCourse = "⭐有课";
                                            else isCourse = "无课";
                                            break;
                                        case 7:
                                            String sunday = userCourse.getSunday();
                                            if(sunday != null && sunday.contains(timeSlot)) isCourse = "⭐有课";
                                            else isCourse = "无课";
                                            break;
                                    }
                                }
                                lateUserList.add(s + "（未打卡 " + isCourse + "）");
                            } else {
                                lateUserList.add(s + "（未打卡）");
                            }
                        }
                    }
                    lateUserList.sort((o1,o2)->{
                        char c1 = o1.charAt(o1.length() - 4);
                        char c2 = o2.charAt(o2.length() - 4);
                        return c1 - c2;
                    });
                    detail.put(USER_LATE_NAMES,lateUserList);
                    lateUserCount = lateUserList.size();
                    return new CheckInfoSingleVO(userCountShould, userCountCurrent,
                            leavesUserCount, lateUserCount, mapBiao, mapDaw,detail);
                },
                Values.ATTENDANCE_INFO_SINGLE_OUT_TIME,
                TimeUnit.HOURS
        );

        if(checkInfoSingleVO == null) {
            if(checkIsOutTime())
                return ResponseResult.DING_ERROR_DING_LONG;
            return ResponseResult.Bad_Request;
        }

        log.info(checkInfoSingleVO.toString());

        return ResponseResult.OK.put(checkInfoSingleVO);

    }

    @Override
    public ResponseResult<?> getAttendanceInfoByTimeSpan(Date from, Date to, List<String> group, Long pageNumber, Long pageSize) {
        if(group == null || group.isEmpty()){
            return ResponseResult.Bad_Request.put("group 参数错误的接收的值为：" + group);
        }

        group.sort((o1,o2)->o2.hashCode()-o1.hashCode());


        List<CheckInfoVO> rs = redisCacheUtil.queryWithPassThroughByHashToList(
                Values.ATTENDANCE_INFO_SPAN,
                "",
                from + "::" + to + "::" + group + "::" + pageNumber.toString() + "::" + pageSize.toString(),
                CheckInfoVO.class,
                () -> {
                    if (checkIsOutTime()) {
                        // 已经过期
                        String andSaveAccessToken = getAndSaveAccessToken();
                        if (andSaveAccessToken == null) {
                            return null;
                        }
                    }

                    List<DingIDDTO> dingIDByGroup;
                    if ("全部".equals(group.get(0))) {
                        dingIDByGroup = userMapper.getDingIDAll();
                    } else {
                        dingIDByGroup = userMapper.getDingIDByGroup(group);
                    }

                    List<String> dingUserIdList = new ArrayList<>();
                    Map<String, String> map = new HashMap<>();
                    for (DingIDDTO dingIDDTO : dingIDByGroup) {
                        dingUserIdList.add(dingIDDTO.getDingID());
                        map.put(dingIDDTO.getDingID(), dingIDDTO.getName() + "." + dingIDDTO.getGroup());
                    }

                    List<OapiAttendanceListResponse.Recordresult> checkInfo = getCheckInfo(from, to, dingUserIdList, pageNumber, pageSize);
                    if (checkInfo != null) {
                        List<CheckInfoVO> checkInfoVOList = new ArrayList<>();
                        for (OapiAttendanceListResponse.Recordresult recordresult : checkInfo) {
                            String userId = recordresult.getUserId();
                            String s = map.get(userId);
                            String[] split = s.split("\\.");
                            String tempName = split[0];
                            String tempGroup = split[1];
                            CheckInfoVO checkInfoVO = new CheckInfoVO(recordresult, tempName, tempGroup);
                            checkInfoVOList.add(checkInfoVO);
                        }
                        return checkInfoVOList;
                    }
                    return null;
                },
                Values.ATTENDANCE_INFO_SPAN_OUT_TIME,
                TimeUnit.DAYS
        );



        if(rs == null) {
            if(checkIsOutTime())
                return ResponseResult.DING_ERROR_DING_LONG;
            return ResponseResult.Bad_Request;
        }

        return ResponseResult.OK.put(rs);
    }

    /*以下是类私有方法，只允许在类内使用*/
    private boolean checkNameExist(List<String> list, String startWitchName){
        return list.stream().anyMatch(s -> s != null && s.startsWith(startWitchName));
    }

    /**
     * 判断两个Date对象的年月日是否相同（忽略时分秒）
     * @param date1 第一个日期
     * @param date2 第二个日期
     * @return 如果年月日相同返回true，否则返回false（包括空值情况）
     */
    public static boolean isSameDate(Date date1, Date date2) {
        // 处理空值情况
        if (date1 == null || date2 == null) {
            return false;
        }

        // 将Date转换为LocalDate（只包含年月日）
        LocalDate localDate1 = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localDate2 = date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // 比较两个LocalDate是否相同
        return localDate1.equals(localDate2);
    }

    /**
     * 根据时间判断属于哪个时段并返回对应输出
     * @param date 要判断的时间
     * @return 对应的时段字符串，如"1-2"、"3-4"等，不在范围内返回"未定义时段"
     */
    public static String getTimeSlot(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // 获取小时和分钟
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        // 转换为分钟数，便于比较
        int totalMinutes = hour * 60 + minute;

        // 定义各个时间段的起始分钟数
        int morningStart1 = 8 * 60;        // 8:00
        int morningEnd1 = 9 * 60 + 40;     // 9:40
        int morningEnd2 = 12 * 60;         // 12:00
        int afternoonStart1 = 14 * 60 + 30;// 14:30 (下午2:30)
        int afternoonEnd1 = 16 * 60 + 20;  // 16:20 (下午4:20)
        int afternoonEnd2 = 18 * 60;       // 18:00 (下午6:00)
        int eveningStart = 20 * 60;        // 20:00 (晚上8:00)
        int eveningEnd = 22 * 60 + 40;     // 22:40 (晚上10:40)

        // 判断属于哪个时间段
        if (totalMinutes >= morningStart1 && totalMinutes < morningEnd1) {
            return "1-2";
        } else if (totalMinutes >= morningEnd1 && totalMinutes < morningEnd2) {
            return "3-4";
        } else if (totalMinutes >= afternoonStart1 && totalMinutes < afternoonEnd1) {
            return "5-6";
        } else if (totalMinutes >= afternoonEnd1 && totalMinutes < afternoonEnd2) {
            return "7-8";
        } else if (totalMinutes >= eveningStart && totalMinutes < eveningEnd) {
            return "9-10";
        } else {
            return "未定义时段";
        }
    }


    private Date[] getDateByShift(Date date, int shift) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // 重置时分秒毫秒，仅保留日期部分
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Date startTime;
        Date endTime;

        switch (shift) {
            case 1:
                // 班次1: 8:00 到 11:40
                calendar.set(Calendar.HOUR_OF_DAY, 8);
                startTime = calendar.getTime();

                calendar.set(Calendar.HOUR_OF_DAY, 11);
                calendar.set(Calendar.MINUTE, 40);
                endTime = calendar.getTime();
                break;
            case 2:
                // 班次2: 15:00 到 18:10 (下午3点到6点10分)
                calendar.set(Calendar.HOUR_OF_DAY, 15);
                startTime = calendar.getTime();

                calendar.set(Calendar.HOUR_OF_DAY, 18);
                calendar.set(Calendar.MINUTE, 10);
                endTime = calendar.getTime();
                break;
            case 3:
                // 班次3: 晚上20:00 到 22:20（晚上8点到10点20分）
                calendar.set(Calendar.HOUR_OF_DAY, 20);  // 20点即晚上8点
                startTime = calendar.getTime();
                calendar.set(Calendar.HOUR_OF_DAY, 22);  // 22点即晚上10点
                calendar.set(Calendar.MINUTE, 20);
                endTime = calendar.getTime();
                break;
            default:
                // 默认返回当天0点和24点
                startTime = calendar.getTime();
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                endTime = calendar.getTime();
        }

        return new Date[]{startTime, endTime};
    }
    /**
     * 获取考勤信息
     */
    private List<OapiAttendanceListResponse.Recordresult> getCheckInfo(Date from, Date to, List<String> userIdList, Long pageNumber, Long pageSize){
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/attendance/list");
            OapiAttendanceListRequest req = new OapiAttendanceListRequest();
            req.setWorkDateFrom(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(from));
            req.setWorkDateTo(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(to));
            req.setUserIdList(userIdList);
            req.setOffset((pageNumber-1)*pageSize);
            req.setLimit(pageSize);
            OapiAttendanceListResponse rsp = client.execute(req, accessToken);
            if(rsp.isSuccess()) {
                return rsp.getRecordresult();
            }
        } catch (Exception error) {
            log.error("获取考勤信息失败",error);
        }
        return null;
    }

    /**
     * 判断token是否过期
     * @return true 过期 false 未过期
     */
    private boolean checkIsOutTime(){
        return (accessToken = redisUtil.getRedisString(Values.ACCESS_TOKEN)) == null;
    }

    /**
     * 获取钉钉的 accessToken
     * @return accessToken
     */
    public String getAndSaveAccessToken(){
        try{
            Config config = new Config();
            config.protocol = "https";
            config.regionId = "central";
            Client client = new Client(config);
            GetAccessTokenRequest getAccessTokenRequest = new GetAccessTokenRequest()
                    .setAppKey(DING_APP_KEY)
                    .setAppSecret(DING_APP_SECRET);
            GetAccessTokenResponse accessTokenTemp = client.getAccessToken(getAccessTokenRequest);
            Integer statusCode = accessTokenTemp.getStatusCode();
            if(statusCode == 200) {
                accessToken = accessTokenTemp.getBody().getAccessToken();
                redisUtil.setRedisString(Values.ACCESS_TOKEN,accessToken,Values.ACCESS_TOKEN_OUT_TIME, TimeUnit.HOURS);
                log.info("钉钉的toke获取成功！！");
                return accessToken;
            }
        } catch (Exception error) {
            log.error("获取钉钉的 accessToken 失败",error);
        }
        return null;
    }

    /**
     * 获取部门 ID 列表
     * @return 部门ID
     */
    private List<Long> getDepartmentIds(){
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/department/listsubid");
            OapiV2DepartmentListsubidRequest req = new OapiV2DepartmentListsubidRequest();
            req.setDeptId(1L);
            OapiV2DepartmentListsubidResponse rsp = client.execute(req, accessToken);
            return rsp.getResult().getDeptIdList();
        } catch (Exception error) {
            log.error("获取部门 ID 列表失败",error);
        }
        return null;
    }

    /**
     * 获取部门下所有的用户的简单信息
     * @param deptId 部门ID
     * @param cursor 起始位置
     * @param size 获取的数量
     * @return 用户的简单信息
     */
    private List<OapiUserListsimpleResponse.ListUserSimpleResponse> getUserSingleInfo(Long deptId,Long cursor,Long size){
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/user/listsimple");
            OapiUserListsimpleRequest req = new OapiUserListsimpleRequest();
            req.setDeptId(deptId);
            req.setCursor(cursor);
            req.setSize(size);
            OapiUserListsimpleResponse rsp = client.execute(req, accessToken);
            return rsp.getResult().getList();
        } catch (Exception error) {
            log.error("获取部门 ID 列表失败",error);
        }
        return null;
    }

    public int getUserLeavesCount(String sb, Date from, Date to, Map<String, String> userIdName,Map<String,List<String>> detail){
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/attendance/getleavestatus");
            OapiAttendanceGetleavestatusRequest req = new OapiAttendanceGetleavestatusRequest();
            req.setUseridList(sb);
            req.setStartTime(from.getTime());
            req.setEndTime(to.getTime());
            req.setOffset(0L);
            req.setSize(20L);
            List<String> leavesUserList = new ArrayList<>();
            Set<String> set = new HashSet<>();
            boolean hasMore = true;
            while(hasMore) {
                OapiAttendanceGetleavestatusResponse rsp = client.execute(req, accessToken);
                if(rsp.isSuccess()){
                    List<OapiAttendanceGetleavestatusResponse.LeaveStatusVO> leaveStatus = rsp.getResult().getLeaveStatus();
                    for (OapiAttendanceGetleavestatusResponse.LeaveStatusVO leaveStatusVO : leaveStatus) {
                        String userid = leaveStatusVO.getUserid();
                        set.add(userid);
                        String name = userIdName.get(userid);
                        leavesUserList.add(name);
                    }
                    hasMore = rsp.getResult().getHasMore();
                    req.setOffset(req.getOffset()+20L);
                    req.setSize(20L);
                } else {
                    Long errcode = rsp.getErrcode();
                    String errmsg = rsp.getErrmsg();
                    log.error("获取请假人数失败，错误码：{}，错误信息：{}",errcode,errmsg);
                    detail.put(USER_LEAVES_NAMES,new ArrayList<>());
                    return 0;
                }
            }
            detail.put(USER_LEAVES_NAMES,leavesUserList);
            return set.size();
        } catch (Exception error) {
            log.error("获取请假人数失败",error);
        }
        detail.put(USER_LEAVES_NAMES,new ArrayList<>());
        return 0;
    }
}
