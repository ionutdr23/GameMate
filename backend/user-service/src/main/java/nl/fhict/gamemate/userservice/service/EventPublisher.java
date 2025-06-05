package nl.fhict.gamemate.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.fhict.gamemate.userservice.config.RabbitMQConfig;
import nl.fhict.gamemate.userservice.event.FriendshipStatusChangedEvent;
import nl.fhict.gamemate.userservice.event.UserStatusChangedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public void publishFriendshipEvent(FriendshipStatusChangedEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    "friendship.status.changed",
                    event
            );
            log.debug("Published FriendshipStatusChangedEvent: {}", event);
        } catch (Exception e) {
            log.error("Failed to publish FriendshipStatusChangedEvent for userId={} and friendId={}",
                    event.getUserId(), event.getFriendId(), e);
        }
    }

    public void publishUserEvent(UserStatusChangedEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    "user.status.changed",
                    event
            );
            log.debug("Published UserStatusChangedEvent: {}", event);
        } catch (Exception e) {
            log.error("Failed to publish UserStatusChangedEvent for userId={}", event.getUserId(), e);
        }
    }
}
