package weilai.team.officialWebSiteApi.config.message;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String USER_EXCHANGE_NAME = "user_message_exchange";
    public static final String ANNO_EXCHANGE_NAME="anno_message_exchange";
    public static final String QUEUE_NAME = "user_queue";
    public static final String ANNOUNCEMENT_QUEUE = "announcement_queue";




    @Bean
    Queue queue2() {
        return new Queue("test_queue", true);
    }

    @Bean
    Binding testBinding() {
        return BindingBuilder.bind(queue2()).to(exchange1()).with("test");
    }


    @Bean
    Queue queue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    Queue announcementQueue() {
        return new Queue(ANNOUNCEMENT_QUEUE, true);
    }

    @Bean
    DirectExchange exchange1() {
        return new DirectExchange(USER_EXCHANGE_NAME,true,false);
    }

    @Bean
    FanoutExchange exchange2(){
        return new FanoutExchange(ANNO_EXCHANGE_NAME,true,false);
    }

    @Bean
    Binding userBinding() {
        return BindingBuilder.bind(queue()).to(exchange1()).with("users");
    }

    @Bean
    Binding announcementBinding() {
        return BindingBuilder.bind(announcementQueue()).to(exchange2());
    }

}
