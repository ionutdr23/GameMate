package nl.fhict.gamemate.socialservice.listener;

import nl.fhict.gamemate.socialservice.config.RabbitMQConfig;
import nl.fhict.gamemate.socialservice.dto.TestEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TestListener {
    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void receive(TestEvent event) {
        System.out.println("Received message: " + event.getMessage());
    }
}
