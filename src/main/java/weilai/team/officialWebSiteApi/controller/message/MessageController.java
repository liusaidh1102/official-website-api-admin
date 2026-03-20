package weilai.team.officialWebSiteApi.controller.message;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import weilai.team.officialWebSiteApi.service.admin.annotation.AutoLog;
import weilai.team.officialWebSiteApi.service.message.MessageService;
import weilai.team.officialWebSiteApi.service.message.sendMessage.SseService;
import weilai.team.officialWebSiteApi.util.ResponseResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@Api(tags = "消息管理")
@RequestMapping("/message")
public class MessageController {

    @Resource
    SseService sseService;

    @Resource
    MessageService messageService;

//    @ApiOperation("连接sse")
//    @GetMapping(value = "sse/addClient/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public SseEmitter addClient(@PathVariable Long userId){
//        return sseService.addClient(userId);
//    }

    @AutoLog
    @ApiOperation("连接sse")
    @GetMapping(value = "sse/addClient", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter addClient(HttpServletRequest request){
        return sseService.addClient(request);
    }

    @AutoLog
    @ApiOperation("获取信息列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "messageType",value = "信息类型 1 点赞/收藏，3评论  5系统通知",required = true,dataType = "String"),
            @ApiImplicitParam(name = "pageNumber",value = "显示页码",required = true,dataType = "String"),
            @ApiImplicitParam(name = "pageSize",value = "每页条数",required = true,dataType = "String")
    })
    @GetMapping("/getMessageInfo")
    public ResponseResult<?>getMessageInfo(Integer messageType,Integer pageNumber,Integer pageSize,HttpServletRequest request){
        return messageService.getMessageInfoByType(messageType,pageNumber,pageSize,request);
    }

    @AutoLog
    @ApiOperation("将所有信息标记为已读")
    @ApiImplicitParam(name = "messageType",value = "信息类型 1 点赞/收藏，3评论  5系统通知",required = true,dataType = "String")
    @PutMapping("/markUnreadAsReadByMessageType")
    public ResponseResult<?> markUnreadAsReadByMessageType(Integer messageType,HttpServletRequest request){
        return messageService.markUnreadAsReadByMessageType(messageType,request);
    }

    @AutoLog
    @ApiOperation("删除所有信息")
    @ApiImplicitParam(name = "messageType",value = "信息类型 1 点赞/收藏，3评论  5系统通知",required = true,dataType = "String")
    @DeleteMapping("/deleteAllMessages")
    public ResponseResult<?>  deleteAllMessages(Integer messageType,HttpServletRequest request){
        return messageService.deleteAllMessages(messageType,request);
    }

    @AutoLog
    @ApiOperation("删除单个信息")
    @ApiImplicitParam(name = "messageId" ,value = "信息ID",required = true,dataType = "String")
    @DeleteMapping("/deleteOneMessage/{messageId}")
    public ResponseResult<?> deleteOneMessage(@PathVariable Long messageId,HttpServletRequest request){
        return messageService.deleteMessageByMessageId(messageId,request);
    }

    @AutoLog
    @ApiOperation("获取未读信息数量")
    @ApiImplicitParam(name = "messageType",value = "信息类型 1 点赞/收藏，3评论  5系统通知",required = true,dataType = "String")
    @GetMapping("/ getNotReadCount")
    public ResponseResult<?> getNotReadCountByMessageType(Integer messageType,HttpServletRequest request){
        return messageService.getNotReadCountByMessageType(messageType,request);
    }
}
