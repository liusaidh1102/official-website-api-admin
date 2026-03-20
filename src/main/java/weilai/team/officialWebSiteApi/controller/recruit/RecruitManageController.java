package weilai.team.officialWebSiteApi.controller.recruit;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;
import weilai.team.officialWebSiteApi.entity.recruit.DTO.ExportDTO;
import weilai.team.officialWebSiteApi.entity.recruit.DTO.PageDTO;
import weilai.team.officialWebSiteApi.entity.recruit.DTO.RecruitQueryDTO;
import weilai.team.officialWebSiteApi.entity.recruit.DTO.UpdateInfoDTO;
import weilai.team.officialWebSiteApi.service.admin.annotation.AutoLog;
import weilai.team.officialWebSiteApi.service.recruit.RecruitManageService;
import weilai.team.officialWebSiteApi.util.ResponseResult;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
/**
 * @author lzw
 * @date 2024/11/11 15:04
 * @description 作用：招新管理的侯选人查询
 */
@RestController
@RequestMapping("/recruit/manage")
@Api(tags = "招新管理", description = " ")
public class RecruitManageController{

    /*
        招新管理的service，对应管理候选人
     */
    @Resource
    private RecruitManageService recruitManageService;


    @AutoLog
    @GetMapping("/listAllRecruitUser")
    @ApiOperation("获取所有提交报名的人员（或按条件查询）")
    public ResponseResult<?> listAllRecruitUser(@Valid RecruitQueryDTO recruitQueryDTO) {
        return recruitManageService.listAllRecruitUser(recruitQueryDTO);
    }

    @AutoLog
    @PutMapping("/updateRecruitUserStatus")
    @ApiOperation("修改招新报名人员的状态")
    public ResponseResult<?> updateRecruitUserStatus(
            @ApiParam(name = "ids",value = "候选人的id", allowMultiple = true) @RequestParam Integer[] ids,
            @ApiParam(name = "interviewStatus", value = "0代表待安排；1代表待面试；2代表已录取；3代表未录取", required = true)@RequestParam Integer interviewStatus) {
        return recruitManageService.updateRecruitUserStatus(ids, interviewStatus);
    }


    @AutoLog
    @PostMapping("/resultExport")
    @ApiOperation("导出全部的录取结果")
    public ResponseResult<?> resultExport(HttpServletResponse response,@RequestBody ExportDTO exportDTO) {
        return recruitManageService.resultExport(response,exportDTO);
    }

    @AutoLog
    @GetMapping("/getResume/{id}")
    @ApiOperation("获取某人的简历")
    public ResponseResult<?> getResume(@PathVariable Integer id) {
        return recruitManageService.getResume(id);
    }


    @AutoLog
    @GetMapping("/listAllGrade")
    @ApiOperation("获取所有的年级")
    public ResponseResult<?> listAllGrade(@Valid PageDTO pageDTO) {
        return recruitManageService.listAllGrade(pageDTO);
    }


    @AutoLog
    @PutMapping("/updateInfo")
    @ApiOperation("修改招新报名人员信息")
    public ResponseResult<?> updateInfo(@RequestBody @Valid UpdateInfoDTO updateInfoDTO) {
        return recruitManageService.updateInfo(updateInfoDTO);
    }

    @AutoLog
    @DeleteMapping("/deleteRecruitUser")
    @ApiOperation("删除候选人(软删除)")
    public ResponseResult<?> deleteRecruitUser(@RequestParam @ApiParam("候选人的id")Integer id) {
        return recruitManageService.deleteRecruitUser(id);
    }


    @AutoLog
    @GetMapping("/getCount/{status}")
    @ApiOperation("获取  待安排/已录取  的数量")
    public ResponseResult<?> getCount(@ApiParam(required = true, value = "0代表待安排，2代表已录取") @PathVariable Integer status) {
        return recruitManageService.getCount(status);
    }


    @AutoLog
    @GetMapping("/getAboutMe")
    @ApiOperation("获取  待我反馈/我录取的/我淘汰的 面试记录")
    public ResponseResult<?> getAboutMe(
            @RequestParam @ApiParam(required = true, value = "1待反馈；2已录取；3未录取") Integer status,
            HttpServletRequest request,
            @Valid PageDTO pageDTO) {
        return recruitManageService.getAboutMe(status,request,pageDTO);
    }

    @AutoLog
    @GetMapping("/getClazz/{grade}")
    @ApiOperation("获取某年级下的班级(2024级)")
    public ResponseResult<?> getClazz(@PathVariable String grade) {
        return recruitManageService.getClazz(grade);
    }

    @AutoLog
    @GetMapping("/getName")
    @ApiOperation("获取快捷操作的相关内容")
    public ResponseResult<?> getName() {
        return recruitManageService.getName();
    }


}
