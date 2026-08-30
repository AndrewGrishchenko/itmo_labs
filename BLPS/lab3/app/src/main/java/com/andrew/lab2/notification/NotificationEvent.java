package com.andrew.lab2.notification;

import java.util.UUID;

import com.andrew.lab2.notification.dto.NotificationPayload;

public record NotificationEvent(
    UUID eventId,
    NotificationEventType eventType,
    Long userId,
    String email,
    NotificationPayload payload
) {}
