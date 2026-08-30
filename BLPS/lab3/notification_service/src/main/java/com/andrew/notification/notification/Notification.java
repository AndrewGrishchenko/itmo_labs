package com.andrew.notification.notification;

import java.time.Instant;
import java.util.UUID;

import com.andrew.notification.event.NotificationEventType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "notifications"
    // uniqueConstraints = {
    //         @UniqueConstraint(
    //             name = "uk_notifications_event_id",
    //             columnNames = "event_id"
    //         )
    //     }
)
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "event_id", nullable = false, unique = true)
    private UUID eventId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private NotificationEventType eventType;

    @Column(nullable = false)
    private String recipient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public Notification(UUID eventId, NotificationEventType eventType, String recipient) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.recipient = recipient;
        this.status = NotificationStatus.PENDING;
        this.createdAt = Instant.now();
    }
}
