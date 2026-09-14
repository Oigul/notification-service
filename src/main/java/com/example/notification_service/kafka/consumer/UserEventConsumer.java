package com.example.notification_service.kafka.consumer;

import com.example.notification_service.kafka.events.UserEvent;
import com.example.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "user-events", groupId = "notification-group")
    public void consume(String message) {
        try {
            log.info("A message was received from Kafka: {}", message);

            UserEvent event = objectMapper.readValue(message, UserEvent.class);

            NotificationService.NotificationTemplate template;
            try {
                template = NotificationService.NotificationTemplate.valueOf(event.getOperation().toUpperCase());
            } catch (IllegalArgumentException e) {
                log.info("Unknown transaction received: {}", event.getOperation());
                return;
            }

            notificationService.sendEmail(event.getEmail(), template.getSubject(), template.getText());

        } catch (Exception e) {
            log.error("Error processing message from Kafka: {}", message, e);
        }
    }
}
