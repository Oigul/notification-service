package com.example.notification_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final JavaMailSender mailSender;

    @Override
    public void sendEmail(String to, String subject, String text) {
        log.info("sendEmail:");
        log.info("to: {}", to);
        log.info("Topic: {}", subject);
        log.info("Text: {}", text);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            message.setFrom("someName@someSite.com");

            mailSender.send(message);

            log.info("The letter has been successfully sent to the address: {}", to);
            log.info("Contents of the letter:\nTopic: {}\nText: {}", subject, text);
        } catch (Exception e) {
            log.error("Failed to send email to address: {}", to, e);
        }
    }
}
