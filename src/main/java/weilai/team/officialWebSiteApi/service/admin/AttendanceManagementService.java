package weilai.team.officialWebSiteApi.service.admin;

import weilai.team.officialWebSiteApi.entity.admin.VO.CheckInfoVO;
import weilai.team.officialWebSiteApi.util.ResponseResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * ClassName:AttendanceManagementService
 * Description:
 *
 * @Author:独酌
 * @Create:2025/3/11 10:55
 */
public interface AttendanceManagementService {

    /**
     * 获取一段时间内的考勤情况
     * @param from 开始时间
     * @param to 结束时间
     * @param group 组别
     * @param pageNumber 页码
     * @param pageSize 每页条数
     * @return 结果集
     */
    ResponseResult<?> getAttendanceInfoByTimeSpan(Date from, Date to, List<String> group, Long pageNumber, Long pageSize);

    /**
     * 获取单个时间内的考勤情况
     * @param time 时间
     * @param group 组别
     * @return 结果集
     */
    ResponseResult<?> getAttendanceInfoBySingleTime(Date time,String group,Integer shift);

}
