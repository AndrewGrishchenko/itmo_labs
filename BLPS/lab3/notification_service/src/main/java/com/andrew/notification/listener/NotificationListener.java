package com.andrew.notification.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.andrew.notification.event.NotificationEvent;
import com.andrew.notification.processor.NotificationProcessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.jms.JMSException;
import jakarta.jms.TextMessage;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationListener {
    private final ObjectMapper objectMapper;
    private final NotificationProcessor processor;

    private final Logger log = LoggerFactory.getLogger(NotificationListener.class);

    @JmsListener(
        destination = "${notification.rabbitmq.queue}",
        containerFactory = "jmsListenerContainerFactory"
    )
    public void receive(TextMessage message) throws JMSException {
        NotificationEvent event;
        
        try {
            event = objectMapper.readValue(message.getText(), NotificationEvent.class);
        } catch (JsonProcessingException e) {
            log.error("Invalid NotificationEvent: ", e.getMessage());
            return;
        }

        processor.process(event);
    }
}
