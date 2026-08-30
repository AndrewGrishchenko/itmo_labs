package com.andrew.lab2.notification;

import java.util.UUID;

import org.springframework.jms.core.JmsClient;
import org.springframework.stereotype.Service;

import com.andrew.lab2.notification.dto.NotificationPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class NotificationProducer {
    private final JmsClient jmsClient;
    private final ObjectMapper objectMapper;

    public void sendNotification(NotificationEventType type, Long userId, String email, NotificationPayload payload) {
        NotificationEvent event = new NotificationEvent(
            UUID.randomUUID(),
            type,
            userId,
            email,
            payload
        );

        try {
            String json = objectMapper.writeValueAsString(event);

            jmsClient
                .destination("notification.events")
                .send(json);
        } catch (JsonProcessingException e) {
            System.err.println("json processing e: " + e);
        }
    }
}
