package weilai.team.officialWebSiteApi.mapper.message;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.data.repository.query.Param;
import weilai.team.officialWebSiteApi.entity.message.DO.Message;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import weilai.team.officialWebSiteApi.entity.message.DTO.MessageDTO;
import weilai.team.officialWebSiteApi.entity.message.DTO.MessagePostDTO;
import weilai.team.officialWebSiteApi.entity.message.VO.MessageVO;

import java.util.Date;
import java.util.List;

/**
* @author someo
* @description 针对表【message(消息表)】的数据库操作Mapper
* @createDate 2024-11-10 10:28:21
* @Entity weilai.team.officialWebSiteApi.entity.message.DO.message
*/
public interface MessageMapper extends BaseMapper<Message> {


    /**
     * 根据接收用户ID来查找评论消息
     * @param receiverId 接收信息用户ID
     * @param messageType 消息类型
     * @return
     */
    List<MessageVO> selectMessageByReceiverId(Page<MessageVO> pageInfo, @Param("receiverId") Long receiverId, @Param("messageType")int messageType);

    /**
     * 根据信息ID查找信息
     * @param messageId 信息ID
     * @return
     */
    Message selectMessageByMessageId(@Param("messageId")Long messageId);
    /**
     * 获取未读数量
     * @param receiverId 接收信息用户ID
     * @return
     */
    int getNotReadMessageCountByMessageType(@Param("receiverId") Long receiverId,@Param("messageType")int messageType);


    /**
     * 获取信息总量
     * @param receiverId 接收信息用户ID
     * @param messageType 消息类型
     * @return
     */
    int getMessageCountByMessageType(@Param("receiverId") Long receiverId,@Param("messageType")int messageType);

    /**
     * 将所有未读标记已读
     * @param receiverId 接收信息用户ID
     */
    void markUnreadMessagesAsReadByMessageType(@Param("receiverId")Long receiverId,@Param("messageType")int messageType);

    /**
     * 删除该用户某一类的所有信息
     * @param receiverId 接收信息用户ID
     */
    void deleteAllMessagesByMessageType(@Param("receiverId")Long receiverId,@Param("messageType")int messageType);

    /**
     * 根据消息ID删除一条信息
     * @param MessageId 消息ID
     */
    void deleteMessageByMessageId(@Param("messageId")Long MessageId);

    /**
     * 根据消息ID获取接收用户ID
     * @param MessageId 消息ID
     * @return
     */
    Long getReceiverIdByMessageId(@Param("messageId")Long MessageId);

    /**
     * 获取时间戳固定的信息数量
     * @param receiverId 接收信息用户ID
     * @param messageType 信息类型
     * @param limit 限制数量
     * @param timeStamp 时间戳
     * @return
     */
    List<MessageVO> getMessageFromTimeStamp(@Param("receiverId")Long receiverId, @Param("messageType")int messageType, @Param("limit") int limit, @Param("timeStamp")Date timeStamp);

    /**
     * 根据分页信息获取MessagePostDTO
     * @param receiverId 接收信息用户ID
     * @param messageType 消息类型
     * @return
     */
    List<MessagePostDTO> getMessagePostDTO(Page<MessagePostDTO> pageInfo, @Param("receiverId") Long receiverId, @Param("messageType")int messageType);

}




