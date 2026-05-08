package weilai.team.officialWebSiteApi.controller.admin;

import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import weilai.team.officialWebSiteApi.service.admin.AttendanceManagementService;
import weilai.team.officialWebSiteApi.service.admin.annotation.AutoLog;
import weilai.team.officialWebSiteApi.util.ResponseResult;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import static weilai.team.officialWebSiteApi.util.Values.*;

/**
 * ClassName:AttendanceManagement
 * Description:
 *
 * @Author:独酌
 * @Create:2025/3/11 10:54
 */
@RestController
@Api(tags = "考勤信息",description = " ")
@RequestMapping("/Attendance")
@CrossOrigin //允许所有请求进来
public class AttendanceManagement {
    @Resource
    private AttendanceManagementService AMService;

    @AutoLog
    @ApiOperation("获取考勤信息 -- 一段时间")
    @GetMapping("/getCheckInfoByTimeSpan")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "from",value = "开始时间(最大间隔7天)",required = true),
            @ApiImplicitParam(name = "to",value = "结束时间",required = true),
            @ApiImplicitParam(name = "pageNumber",value = "页码",required = true),
            @ApiImplicitParam(name = "pageSize",value = "每页条数",required = true)
    })
    public ResponseResult<?> getCheckInfoByTimeSpan(Date from,Date to, String group, Long pageNumber, Long pageSize){
        return AMService.getAttendanceInfoByTimeSpan(from,to,Arrays.asList(group),pageNumber,pageSize);
    }

    @AutoLog
    @ApiOperation("获取考勤信息 -- 单个时间")
    @GetMapping("/getAttendanceInfoBySingleTime")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "time",value = "时间",required = true),
            @ApiImplicitParam(name = "group",value = "组别（传入”全部“，即查询全部）",required = true),
            @ApiImplicitParam(name = "shift",value = "班次（0:当天一天，1:仅统计上午，2:仅统计下午，3:仅统计晚上  的签到情况）",required = true)
    })
    public ResponseResult<?> getAttendanceInfoBySingleTime(Date time,String group,Integer shift){
        return AMService.getAttendanceInfoBySingleTime(time,group,shift);
    }
}
