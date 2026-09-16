package com.example.notification_service.controller;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.mail.host=localhost",
        "spring.mail.port=3025", // Стандартный порт ServerSetupTest.SMTP
        "spring.mail.properties.mail.transport.protocol=smtp"
})
public class NotificationControllerIT {

    //тестовый SMTP-сервер
    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP);

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void resetMail() {
        greenMail.reset();
    }

    @Test
    void sendNotification_CreateUser_Successfully() throws Exception {

        String requestJson = "{\"email\":\"anna@example.com\",\"operation\":\"CREATE\"}";

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Notification sent successfully"));

        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(1, receivedMessages.length);

        MimeMessage message = receivedMessages[0];

        String recipientEmail = message.getAllRecipients()[0].toString();
        assertEquals("anna@example.com", recipientEmail);

        assertEquals("Welcome", message.getSubject());

        String mailContent = message.getContent().toString();
        assertTrue(mailContent.contains("Ваш аккаунт на сайте ваш сайт был успешно создан"));
    }

    @Test
    void sendNotification_DeleteUser_Successfully() throws Exception {
        String requestJson = "{\"email\":\"anna@example.com\",\"operation\":\"DELETE\"}";

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Notification sent successfully"));

        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(1, receivedMessages.length);

        MimeMessage message = receivedMessages[0];
        assertEquals("anna@example.com", message.getAllRecipients()[0].toString());
        assertEquals("Notice of removal", message.getSubject());
        assertTrue(message.getContent().toString().contains("Ваш аккаунт был удалён"));
    }

    @Test
    void sendNotification_UnknownOperation() throws Exception {
        String requestJson = "{\"email\":\"anna@example.com\",\"operation\":\"unknown\"}";

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(0, receivedMessages.length);
    }

    @Test
    void sendNotification_InvalidJson() throws Exception {
        String brokenJson = "{\"invalid-json\"";

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(brokenJson))
                        .andExpect(status().isBadRequest());
    }
}
