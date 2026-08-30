package com.andrew.notification.config;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.andrew.email.EmailConnectionFactory;

@Configuration
public class EmailJcaConfig {
    @Bean
    public EmailConnectionFactory emailConnectionFactory() throws NamingException {
        InitialContext context = new InitialContext();

        return (EmailConnectionFactory) context.lookup("java:/eis/EmailConnectionFactory");
    }
}
