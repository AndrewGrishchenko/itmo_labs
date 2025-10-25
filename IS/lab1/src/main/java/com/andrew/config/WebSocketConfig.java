package com.andrew.config;

import com.andrew.websocket.CoordinatesSocketServer;
import com.andrew.websocket.LocationSocketServer;
import com.andrew.websocket.MovieSocketServer;
import com.andrew.websocket.PersonSocketServer;

import jakarta.websocket.Endpoint;
import jakarta.websocket.server.ServerApplicationConfig;
import jakarta.websocket.server.ServerEndpointConfig;

import java.util.HashSet;
import java.util.Set;

public class WebSocketConfig implements ServerApplicationConfig {
    @Override
    public Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> endpointClasses) {
        return null;
    }

    @Override
    public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> scanned) {
        Set<Class<?>> results = new HashSet<>();
        
        results.add(CoordinatesSocketServer.class);
        results.add(LocationSocketServer.class);
        results.add(PersonSocketServer.class);
        results.add(MovieSocketServer.class);
        
        return results;
    }
}