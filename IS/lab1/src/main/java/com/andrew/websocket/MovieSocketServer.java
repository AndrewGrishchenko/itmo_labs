package com.andrew.websocket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/websocket/movie")
public class MovieSocketServer {
    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }

    public static void broadcast(WebSocketMessage<?> message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);

            sessions.forEach(session -> {
                try {
                    session.getBasicRemote().sendText(jsonMessage);
                } catch (IOException e) {
                    System.err.println("error during sending message via websocket: " + e.getMessage());
                }
            });   
        } catch (IOException e) {
            System.err.println("unable to serialize msg: " + e.getMessage());
        }
    }
}
