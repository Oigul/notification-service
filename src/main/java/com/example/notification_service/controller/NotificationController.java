package com.example.notification_service.controller;

import com.example.notification_service.dto.NotificationResponse;
import com.example.notification_service.kafka.events.UserEvent;
import com.example.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public NotificationResponse sendNotification(@RequestBody UserEvent event) {
        String email = event.getEmail();
        String operation = event.getOperation();

        log.info("NotificationController::sendNotification email: {} text: {}", email, operation);

        NotificationService.NotificationTemplate template = NotificationService.NotificationTemplate.valueOf(operation.toUpperCase());

        boolean sent = notificationService.sendEmail(email, template.getSubject(), template.getText());

        return sent ? new NotificationResponse(true, "Notification sent successfully")
                : new NotificationResponse(false, "Failed to send email");
    }
}
