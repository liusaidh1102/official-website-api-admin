package weilai.team.officialWebSiteApi.controller.recruit;
import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import weilai.team.officialWebSiteApi.entity.recruit.DTO.PageDTO;
import weilai.team.officialWebSiteApi.entity.recruit.DTO.RecruitCommentDTO;
import weilai.team.officialWebSiteApi.entity.recruit.DTO.RecruitInterviewDTO;
import weilai.team.officialWebSiteApi.entity.recruit.DTO.ScheduleInterviewDTO;
import weilai.team.officialWebSiteApi.service.admin.annotation.AutoLog;
import weilai.team.officialWebSiteApi.service.recruit.RecruitInterviewService;
import weilai.team.officialWebSiteApi.util.ResponseResult;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
/**
 * @author lzw
 * @date 2024/11/11 15:03
 * @description 作用：招新面试的controller
 */
@RestController
@RequestMapping("recruit/interview")
@Api(tags = "招新面试",description = "")
public class RecruitInterviewController {

    @Resource
    private RecruitInterviewService recruitInterviewService;

    @AutoLog
    @PostMapping("/scheduleInterviewer")
    @ApiOperation("安排面试官")
    public ResponseResult<?> scheduleInterviewer(@RequestBody @Valid ScheduleInterviewDTO scheduleInterviewDTO){
        return recruitInterviewService.scheduleInterviewer(scheduleInterviewDTO);
    }


    @AutoLog
    @GetMapping("/listAllInterview")
    @ApiOperation("获取面试的人员，或按照条件筛选")
    public ResponseResult<?> listAllInterview(@Valid RecruitInterviewDTO interviewDTO, HttpServletRequest request) {
        return recruitInterviewService.listAllInterview(interviewDTO,request);
    }


    @AutoLog
    @PostMapping("/comment")
    @ApiOperation("面试官评价")
    public ResponseResult<?> insertComment(@Valid @RequestBody RecruitCommentDTO recordDTO){
        return recruitInterviewService.insertComment(recordDTO);
    }


    @AutoLog
    @GetMapping("/listAllHr")
    @ApiOperation("获取所有面试官")
    @ApiImplicitParam(name = "name",value = "面试官的名字(查询)",dataType = "String")
    public ResponseResult<?> listAllHr(@Valid PageDTO pageDTO, String name){
        return recruitInterviewService.listAllHr(pageDTO,name);
    }


    @AutoLog
    @GetMapping("/getComment/{id}")
    @ApiOperation("根据面试记录的id获取面评")
    @ApiImplicitParam(name = "id",value = "根据id查询",dataType = "Integer")
    public ResponseResult<?> getComment(@PathVariable Integer id){
        return recruitInterviewService.getComment(id);
    }

}
