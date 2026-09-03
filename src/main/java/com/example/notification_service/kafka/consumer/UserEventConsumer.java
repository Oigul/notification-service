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

            String email = event.getEmail();
            String operation = event.getOperation();

            if ("CREATE".equalsIgnoreCase(operation)) {
                String subject = "Welcome";
                String text = "Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан.";

                notificationService.sendEmail(email, subject, text);

            } else if ("DELETE".equalsIgnoreCase(operation)) {
                String subject = "Notice of removal";
                String text = "Здравствуйте! Ваш аккаунт был удалён.";

                notificationService.sendEmail(email, subject, text);
            } else {
                log.info("Unknown transaction received: {}", operation);
            }

        } catch (Exception e) {
            log.error("Error processing message from Kafka: {}", message, e);
        }
    }
}
