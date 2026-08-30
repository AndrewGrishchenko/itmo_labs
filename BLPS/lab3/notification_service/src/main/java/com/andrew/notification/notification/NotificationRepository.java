package com.andrew.notification.notification;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Optional<Notification> findByEventIdAndEventTypeAndRecipient(
        UUID eventId,
        String eventType,
        String recipient
    );

    boolean existsByEventId(UUID eventId);

    Optional<Notification> findByEventId(UUID eventId);
}
