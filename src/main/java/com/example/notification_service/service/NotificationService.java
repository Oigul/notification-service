package com.example.notification_service.service;

public interface NotificationService {
    boolean sendEmail(String to, String subject, String text);

    enum NotificationTemplate {
        CREATE("Welcome", "Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан."),
        DELETE("Notice of removal", "Здравствуйте! Ваш аккаунт был удалён.");

        private final String subject;
        private final String text;

        NotificationTemplate(String subject, String text) {
            this.subject = subject;
            this.text = text;
        }

        public String getSubject() {
            return subject;
        }

        public String getText() {
            return text;
        }
    }
}
