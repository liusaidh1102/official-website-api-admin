package weilai.team.officialWebSiteApi.service.message;


import weilai.team.officialWebSiteApi.entity.post.DTO.UpdatePostDto;
import weilai.team.officialWebSiteApi.util.ResponseResult;

import javax.servlet.http.HttpServletRequest;

/**
* @author someo
* @description 针对表【message(消息表)】的数据库操作Service
* @createDate 2024-11-10 10:28:21
*/
public interface MessageService {

    /**
     * 根据接收用户ID来查找某类消息
     * @param messageType 信息类型
     * @param request
     * @return
     */
    ResponseResult<?> getMessageInfoByType(int messageType,int page,int size,HttpServletRequest request);


    /**
     * 获取某类未读信息数量
     * @param messageType 信息类型
     * @param request
     * @return
     */
    ResponseResult<?> getNotReadCountByMessageType(int messageType,HttpServletRequest request);

    /**
     * 将某类未读信息标记为已读
     * @param messageType 信息类型
     * @param request
     * @return
     */
    ResponseResult<?> markUnreadAsReadByMessageType(int messageType,HttpServletRequest request);

    /**
     * 删除该用户所有评论
     * @param messageType 信息类型
     * @param request
     * @return
     */
    ResponseResult<?> deleteAllMessages(int messageType,HttpServletRequest request);

    /**
     * 根据消息ID删除一条信息
     * @param  messageId 信息ID
     * @param request
     * @return
     */
    ResponseResult<?> deleteMessageByMessageId(Long messageId,HttpServletRequest request);
}
