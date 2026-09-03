package com.example.notification_service.service;

public interface NotificationService {
    void sendEmail(String to, String subject, String text);
}
