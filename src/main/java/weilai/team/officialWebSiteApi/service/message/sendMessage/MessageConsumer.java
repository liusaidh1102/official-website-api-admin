package weilai.team.officialWebSiteApi.service.message.sendMessage;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import weilai.team.officialWebSiteApi.entity.message.DTO.MessageDTO;
import weilai.team.officialWebSiteApi.entity.message.DTO.MessageNoticeDTO;

import java.io.IOException;

@Slf4j
@Service
public class MessageConsumer {

    @Autowired
    SseService sseService;

    /**
     * 监听用户信息，将监听到的信息通过sse发送
     * @param messageDTO 信息
     */
    @RabbitListener(queues = "user_queue", ackMode = "MANUAL")
    public void receiveUserMessage(MessageDTO messageDTO, @Header(AmqpHeaders.DELIVERY_TAG) long tag, Channel channel) throws IOException {
        try {
            log.info("监听到一条信息");
            sseService.sendToUser(messageDTO.getMessageVO(), messageDTO.getReceiverId());
            channel.basicAck(tag, false);  // 手动应答确认消息
        } catch (Exception e) {
            channel.basicNack(tag, false, true);  // 失败时重新投递消息
            log.error("消息处理失败", e);
        }
    }

    /**
     * 监听公告信息
     * @param messageNoticeDTO 公告信息
     */
    @RabbitListener(queues = "announcement_queue", ackMode = "MANUAL")
    public void receiveAnnouncementMessage(MessageNoticeDTO messageNoticeDTO,
                                           @Header(AmqpHeaders.DELIVERY_TAG) long tag,
                                           Channel channel) throws IOException {
        try {
            log.info("监听到一条公告信息");
            // 发送公告信息
            sseService.sendToAll(messageNoticeDTO);
            // 确认消息
            channel.basicAck(tag, false);
        } catch (Exception e) {
            // 失败时重新投递消息
            channel.basicNack(tag, false, true);
            log.error("公告信息处理失败", e);
        }
    }
}