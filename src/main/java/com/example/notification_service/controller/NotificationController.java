package com.example.notification_service.controller;

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

    // POST http://localhost:8081/api/notifications
    @PostMapping
    public boolean sendNotification(@RequestBody UserEvent event) {
        String email = event.getEmail();
        String operation = event.getOperation();

        log.info("NotificationController::sendNotification email: {} text: {}", email, operation);

        if ("CREATE".equalsIgnoreCase(operation)) {
            notificationService.sendEmail(
                    email,
                    "Welcome",
                    "Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан."
            );
            return true;
        } else if ("DELETE".equalsIgnoreCase(operation)) {
            notificationService.sendEmail(
                    email,
                    "Notice of removal",
                    "Здравствуйте! Ваш аккаунт был удалён."
            );
            return true;
        }
        return false;
    }
}
