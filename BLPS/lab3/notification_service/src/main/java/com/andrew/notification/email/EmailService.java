package com.andrew.notification.email;

import org.springframework.stereotype.Service;

import com.andrew.email.Email;
import com.andrew.email.EmailConnection;
import com.andrew.email.EmailConnectionFactory;

import jakarta.resource.ResourceException;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class EmailService {
    private final EmailConnectionFactory connectionFactory;

    public void send(String to, String subject, String body) throws ResourceException {
        EmailConnection connection = connectionFactory.getConnection();

        try {
            connection.send(new Email("noreply@video-service.com", to, subject, body));
        } finally {
            connection.close();
        }
    }
}
