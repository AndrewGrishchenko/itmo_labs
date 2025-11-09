package com.andrew.websocket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.Session;

@ApplicationScoped
public class WebSocketNotifier {
    private final ConcurrentMap<String, Set<Session>> topicSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void subscribe(String topic, Session session) {
        topicSessions.computeIfAbsent(topic, k -> Collections.synchronizedSet(new HashSet<>())).add(session);
    }

    public void unsubscribe(String topic, Session session) {
        Set<Session> sessions = topicSessions.get(topic);
        if (sessions != null) {
            sessions.remove(session);

            if (sessions.isEmpty()) {
                topicSessions.remove(topic);
            }
        }
    }

    public void broadcast(String topic, WebSocketMessage<?> message) {
        Set<Session> sessions = topicSessions.get(topic);
        if (sessions == null || sessions.isEmpty()) return;

        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            sessions.forEach(session -> {
                try {
                    if (session.isOpen()) {
                        session.getBasicRemote().sendText(jsonMessage);
                    }
                } catch (IOException e) {
                    System.err.println("unable to notify ws subscriber");
                }
            });
        } catch (JsonProcessingException e) {
            System.err.println("unable to json message");
        }
    }
}
