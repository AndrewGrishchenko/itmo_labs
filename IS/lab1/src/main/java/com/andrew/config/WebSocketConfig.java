// src/main/java/com/andrew/config/WebSocketConfig.java

package com.andrew.config; // Пример пакета

import com.andrew.websocket.CoordinatesSocketServer; // <-- ВАЖНО: импортируйте ваш класс
import com.andrew.websocket.LocationSocketServer;
import com.andrew.websocket.MovieSocketServer;
import com.andrew.websocket.PersonSocketServer;

import jakarta.websocket.Endpoint;
import jakarta.websocket.server.ServerApplicationConfig;
import jakarta.websocket.server.ServerEndpointConfig;

import java.util.HashSet;
import java.util.Set;

/**
 * Этот класс будет автоматически найден сервером приложений.
 * Он скажет контейнеру WebSocket, какие классы нужно развернуть как эндпоинты.
 */
public class WebSocketConfig implements ServerApplicationConfig {

    /**
     * Этот метод позволяет регистрировать эндпоинты, созданные программно.
     * Нам он не нужен, поэтому возвращаем null или пустой Set.
     */
    @Override
    public Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> endpointClasses) {
        return null;
    }

    /**
     * ЭТО ГЛАВНЫЙ МЕТОД!
     * Он возвращает Set всех классов, аннотированных @ServerEndpoint, которые нужно активировать.
     */
    @Override
    public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> scanned) {
        // Создаем новый Set, чтобы не изменять оригинальный `scanned`
        Set<Class<?>> results = new HashSet<>();
        
        // Явно добавляем наш класс эндпоинта
        results.add(CoordinatesSocketServer.class);
        results.add(LocationSocketServer.class);
        results.add(PersonSocketServer.class);
        results.add(MovieSocketServer.class);
        
        // Если у вас будут другие эндпоинты, добавляйте их сюда же:
        // results.add(AnotherSocketServer.class);
        
        System.out.println("====== РЕГИСТРАЦИЯ WEBSOCKET ENDPOINTS ======");
        results.forEach(cls -> System.out.println("Найдено и зарегистрировано: " + cls.getName()));
        System.out.println("==========================================");
        
        return results;
    }
}