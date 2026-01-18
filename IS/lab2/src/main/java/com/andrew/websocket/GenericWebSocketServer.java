package com.andrew.websocket;

import jakarta.inject.Inject;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/websocket/{topic}")
public class GenericWebSocketServer {
    @Inject
    private WebSocketNotifier notifier;

    private String topic;
    private Session session;

    @OnOpen
    public void onOpen(Session session, @PathParam("topic") String topic) {
        this.topic = topic;
        this.session = session;
        notifier.subscribe(topic, session);
    }

    @OnClose
    public void onClose() {
        notifier.unsubscribe(this.topic, this.session);
    }
}
