package com.andrew.lab2.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rabbitmq.jms.admin.RMQConnectionFactory;

import jakarta.jms.ConnectionFactory;

@Configuration 
public class RabbitJmsConfig {
    @Bean
    public ConnectionFactory connectionFactory(
            @Value("${notification.rabbitmq.host}") String host,
            @Value("${notification.rabbitmq.port}") int port,
            @Value("${notification.rabbitmq.username}") String username,
            @Value("${notification.rabbitmq.password}") String password) {

        RMQConnectionFactory factory = new RMQConnectionFactory();

        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);

        return factory;
    }
}
