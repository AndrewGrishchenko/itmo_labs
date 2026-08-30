package com.andrew.notification.notification;

import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.andrew.notification.event.NotificationEvent;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class NotificationService {
    private final NotificationRepository repository;

    @Transactional
    public Optional<Notification> getOrCreateForProcessing(NotificationEvent event) {
        Optional<Notification> existing = repository.findByEventId(event.eventId());

        if (existing.isPresent()) {
            Notification n = existing.get();

            if (n.getStatus() == NotificationStatus.SENT) {
                return Optional.empty();
            }

            return Optional.of(n);
        }

        try {
            Notification notification = new Notification(
                event.eventId(),
                event.eventType(),
                event.email()
            );

            return Optional.of(notification);
        } catch (DataIntegrityViolationException e) {
            return repository.findByEventId(event.eventId())
                .filter(n -> n.getStatus() != NotificationStatus.SENT);
        }
    }

    @Transactional
    public void markProcessing(Notification notification) {
        notification.setStatus(NotificationStatus.PROCESSING);
        repository.save(notification);
    }

    @Transactional
    public void markSent(Notification notification) {
        notification.setStatus(NotificationStatus.SENT);
        repository.save(notification);
    }

    @Transactional
    public void markFailed(Notification notification) {
        notification.setStatus(NotificationStatus.FAILED);
        repository.save(notification);
    }
}
