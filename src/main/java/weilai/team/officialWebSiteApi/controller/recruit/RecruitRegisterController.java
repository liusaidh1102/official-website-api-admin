package weilai.team.officialWebSiteApi.controller.recruit;
import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import weilai.team.officialWebSiteApi.entity.recruit.DTO.RecruitFormDTO;
import weilai.team.officialWebSiteApi.entity.recruit.LimitRequest;
import weilai.team.officialWebSiteApi.service.admin.annotation.AutoLog;
import weilai.team.officialWebSiteApi.service.recruit.RecruitRegisterService;
import weilai.team.officialWebSiteApi.util.*;
import javax.annotation.Resource;
/**
 * @author lzw
 * @date 2024/11/11 15:02
 * @description 招新报名的控制器
 */
@RestController
@ResponseBody
@RequestMapping("/recruit/user")
@Api(tags = "招新报名", description = "")
public class RecruitRegisterController {


    @AutoLog
    @PostMapping(value = "/register",  consumes = "multipart/form-data")
    @ApiOperation(value = "招新报名")
    @LimitRequest(count = 5,time = 60000)
    @ApiResponses(value = {
            @ApiResponse(code = -1, message = "请勿频发发起请求！"),
            @ApiResponse(code = 200, message = "报名成功"),
            @ApiResponse(code = 500, message = "服务器内部错误，请联系管理员！"),
            @ApiResponse(code = 1004, message = "邮箱格式错误！"),
            @ApiResponse(code = 1008, message = "验证码错误！"),
            @ApiResponse(code = 3002, message = "请求参数为空！"),
            @ApiResponse(code = 3100, message = "学号应为11位！"),
            @ApiResponse(code = 3101, message = "性别错误！"),
            @ApiResponse(code = 3102, message = "请务重复报名！"),
            @ApiResponse(code = 3103, message = "文件类型不符！"),
            @ApiResponse(code = 3104, message = "文件大小超出限制！"),
            @ApiResponse(code = 3105, message = "qq号不合法！")
    })
    public ResponseResult<?> recruitRegister(@Validated RecruitFormDTO recruitFormDTO) {
        return recruitRegisterService.recruitRegister(recruitFormDTO);
    }
    /*
     * 注入招新报名的service
     */

    @Resource
    private RecruitRegisterService recruitRegisterService;


    @AutoLog
    @GetMapping("/listAllClass")
    @ApiOperation("获取所有支持报名的班级")
    public ResponseResult<?> listAllClass() {
        return recruitRegisterService.listAllClass();
    }



    @AutoLog
    @GetMapping("/getModel")
    @ApiOperation("获取招新报名的简历模版")
    public ResponseResult<?> getModel() {
        return recruitRegisterService.getModel();
    }


    @AutoLog
    @GetMapping("/sendEmailCode/{email}")
    @ApiOperation("报名发送验证码")
    @ApiImplicitParam(name = "email", value = "邮箱地址", required = true, dataType = "String")
    public ResponseResult<?> sendCaptcha(@PathVariable String email) {
        return recruitRegisterService.sendCaptcha(email);
    }
}
