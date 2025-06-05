package nl.fhict.gamemate.socialservice.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "user.events.exchange";
    public static final String FRIENDSHIP_QUEUE = "friendship.status.changed";
    public static final String USER_QUEUE = "user.status.changed";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue friendshipStatusChangedQueue() {
        return new Queue(FRIENDSHIP_QUEUE, true);
    }

    @Bean
    public Queue userDeletedQueue() {
        return new Queue(USER_QUEUE, true);
    }

    @Bean
    public Binding bindFriendshipQueue() {
        return BindingBuilder.bind(friendshipStatusChangedQueue())
                .to(exchange())
                .with("friendship.status.changed");
    }

    @Bean
    public Binding bindUserQueue() {
        return BindingBuilder.bind(userDeletedQueue())
                .to(exchange())
                .with("user.status.changed");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }
}
