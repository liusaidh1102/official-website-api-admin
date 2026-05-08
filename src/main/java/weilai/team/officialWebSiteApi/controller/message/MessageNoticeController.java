package weilai.team.officialWebSiteApi.controller.message;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import weilai.team.officialWebSiteApi.entity.message.DTO.UpdateNoticeDTO;
import weilai.team.officialWebSiteApi.entity.post.DTO.UpdatePostDto;
import weilai.team.officialWebSiteApi.service.admin.annotation.AutoLog;
import weilai.team.officialWebSiteApi.service.message.MessageUserNoticeService;
import weilai.team.officialWebSiteApi.util.ResponseResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Api(tags = "公告相关操作")
@RequestMapping("/notice")
public class MessageNoticeController {

    @Resource
    MessageUserNoticeService messageUserNoticeService;

    @AutoLog
    @ApiOperation("获取公告列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "pageNumber",value = "显示页码",required = true,dataType = "String"),
            @ApiImplicitParam(name = "pageSize",value = "每页条数",required = true,dataType = "String")
    })
    @GetMapping("/getNoticeInfo")
    public ResponseResult<?> getNoticeInfo(Integer pageNumber,Integer pageSize,HttpServletRequest request){
        return messageUserNoticeService.getNotice(pageNumber,pageSize,request);
    }

    @AutoLog
    @ApiOperation("修改公告")
    @PutMapping("/updateNotice")
    public ResponseResult<?> updateNotice(UpdateNoticeDTO updateNoticeDTO, HttpServletRequest request){
        return messageUserNoticeService.updateNotice(updateNoticeDTO,request);
    }

    @AutoLog
    @ApiOperation("将单个公告标记已读")
    @ApiImplicitParam(name = "noticeId" ,value = "公告ID",required = true,dataType = "String")
    @PutMapping("/markUnReadNoticeAsRead/{noticeId}")
    public ResponseResult<?> markUnReadNoticeAsRead(@PathVariable Long noticeId,HttpServletRequest request){
        return messageUserNoticeService.markUnReadNoticeAsRead(noticeId,request);
    }

    @AutoLog
    @ApiOperation("将所有公告标记已读")
    @PutMapping("/markAllUnReadNoticeAsRead")
    public ResponseResult<?> markAllUnReadNoticeAsRead(HttpServletRequest request){
        return messageUserNoticeService.markAllUnReadNoticeAsRead(request);
    }

    @AutoLog
    @ApiOperation("删除公告")
    @ApiImplicitParam(name = "noticeId" ,value = "公告ID",required = true,dataType = "String")
    @DeleteMapping ("/deleteNotice/{noticeId}")
    public ResponseResult<?> deleteNotice(@PathVariable Long noticeId, HttpServletRequest request){
        return messageUserNoticeService.deleteNotice(noticeId,request);
    }

    @AutoLog
    @ApiOperation("批量删除公告")
    @DeleteMapping ("/batchDeleteNotices")
    public ResponseResult<?> batchDeleteNotices(@PathVariable List<Long> noticeIds, HttpServletRequest request){
        return messageUserNoticeService.batchDeleteNotices(noticeIds,request);
    }

    @AutoLog
    @ApiOperation("获取未读公告数量")
    @GetMapping("/getNotReadCount")
    public ResponseResult<?> getNotReadCount(HttpServletRequest request){
        return messageUserNoticeService.getNotReadNoticeCount(request);
    }

    @AutoLog
    @ApiOperation("根据公告ID获取公告")
    @ApiImplicitParam(name = "noticeId" ,value = "公告ID",required = true,dataType = "String")
    @GetMapping ("/getNoticeById/{noticeId}")
    public ResponseResult<?> getNoticeById(@PathVariable Long noticeId, HttpServletRequest request){
        return messageUserNoticeService.getNoticeById(noticeId,request);
    }

    @AutoLog
    @ApiOperation("获取总公告数量")
    @GetMapping("/getAllNoticeCount")
    public ResponseResult<?> getAllNoticeCount(HttpServletRequest request){
        return messageUserNoticeService.getAllNoticeCount(request);
    }


}
