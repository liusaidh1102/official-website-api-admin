package weilai.team.officialWebSiteApi.service.message.sendMessage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import weilai.team.officialWebSiteApi.config.message.RabbitMQConfig;
import weilai.team.officialWebSiteApi.entity.admin.DO.User;
import weilai.team.officialWebSiteApi.entity.message.DO.Message;
import weilai.team.officialWebSiteApi.entity.message.DTO.MessageDTO;
import weilai.team.officialWebSiteApi.entity.message.DTO.MessageNoticeDTO;
import weilai.team.officialWebSiteApi.entity.message.DTO.MessagePostDTO;
import weilai.team.officialWebSiteApi.entity.message.VO.MessageVO;
import weilai.team.officialWebSiteApi.entity.post.DTO.PostNoticeDto;
import weilai.team.officialWebSiteApi.mapper.message.MessageMapper;
import weilai.team.officialWebSiteApi.mapper.message.MessageUserNoticeMapper;
import weilai.team.officialWebSiteApi.mapper.post.PostMapper;
import weilai.team.officialWebSiteApi.util.RedisUtil;
import weilai.team.officialWebSiteApi.util.UserUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.Date;

import static weilai.team.officialWebSiteApi.util.Values.*;

@Slf4j
@Service
public class MessageProducer {

    private final RabbitTemplate rabbitTemplate;

    @Resource
    RedisUtil redisUtil;

    @Resource
    MessageMapper messageMapper;

    @Resource
    PostMapper postMapper;

    @Resource
    UserUtil userUtil;

    @Resource
    MessageUserNoticeMapper messageUserNoticeMapper;

    @Resource  // 注入 StringRedisTemplate 类
    private StringRedisTemplate redis;

    @Autowired
    public MessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 给某个用户发送信息
     * @param message
     */
    public boolean sendToUser(Message message, HttpServletRequest request) {
        int messageType=message.getMessageType();
        Long receiverId=message.getReceiverId();
        message.setCreatedAt(new Date());
        //插入mysql数据库
        int n=messageMapper.insert(message);
        String postTitle= postMapper.selectPostTitle(message.getPostId());
        MessageVO messageVO =new MessageVO(message,userUtil.getUserInfo(request).getName(), postTitle,userUtil.getUserInfo(request).getHeadPortrait());
        //存入redis中
        String ZSetName ="";
        String stringName1="";
        String stringName2="";
        switch (messageType){
            case 1:
            case 2:
                ZSetName =REDIS_LIKE_OR_COLLECT_PREFIX+receiverId;
                stringName1=REDIS_NOT_READ_LIKE_OR_COLLECT_COUNT_PREFIX+receiverId;
                stringName2=REDIS_ALL_LIKE_OR_COLLECT_COUNT_PREFIX+receiverId;
                break;
            case 3:
            case 4:
                ZSetName =REDIS_COMMENT_PREFIX+receiverId;
                stringName1=REDIS_NOT_READ_COMMENT_COUNT_PREFIX+receiverId;
                stringName2=REDIS_ALL_COMMENT_COUNT_PREFIX+receiverId;
                break;
            case 5:
                ZSetName=REDIS_SYSTEM_NOTIFICATION_PREFIX+receiverId;
                stringName1=REDIS_NOT_READ_SYSTEM_NOTIFICATION_COUNT_PREFIX+receiverId;
                stringName2=REDIS_ALL_SYSTEM_NOTIFICATION_COUNT_PREFIX+receiverId;
                break;
        }
        MessagePostDTO messagePostDTO=new MessagePostDTO(message,postTitle);
        boolean a= redisUtil.setRedisZSetWithOutTime(ZSetName,message.getMessageId(), messagePostDTO);
        //添加未读信息数量
        redisUtil.incrementMessageCount(stringName1,messageType,receiverId);
        //添加总信息数量
        redisUtil.incrementMessageCount(stringName2,messageType,receiverId);
        MessageDTO messageDTO=new MessageDTO(messageVO,receiverId);
        if(n==1&&a){
            rabbitTemplate.convertAndSend(RabbitMQConfig.USER_EXCHANGE_NAME, "users", messageDTO);
            log.info("rabbitmq");
            return true;
        }else {
            return false;
        }

    }

    /**
     * 发布公告
     * @param postNoticeDto 公告信息
     * @param request
     */
    public void sendToAll(PostNoticeDto postNoticeDto,HttpServletRequest request){
        User user=userUtil.getUserInfo(request);
        //将数据插入mysql中
        messageUserNoticeMapper.insertAllUserNotice(postNoticeDto.getNoticeId());
        MessageNoticeDTO messageNoticeDTO =new MessageNoticeDTO(postNoticeDto, user.getName(),user.getHeadPortrait());
        //将数据存入redis中
        String ZSetName=REDIS_ALL_NOTICE;
        String stringName1 =REDIS_NOTICE_COUNT;
        redisUtil.setRedisZSetWithOutTime(ZSetName, postNoticeDto.getNoticeId(), postNoticeDto);
        redisUtil.incrementMessageCount(stringName1,11,null);

        //生产信息
        rabbitTemplate.convertAndSend(RabbitMQConfig.ANNO_EXCHANGE_NAME, "", messageNoticeDTO);
    }
}