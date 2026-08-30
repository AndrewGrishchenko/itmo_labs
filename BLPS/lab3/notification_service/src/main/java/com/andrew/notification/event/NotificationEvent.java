package com.andrew.notification.event;

import java.util.UUID;

import com.andrew.notification.event.dto.NotificationPayload;
import com.fasterxml.jackson.annotation.JsonProperty;

public record NotificationEvent(
    @JsonProperty(required = true) UUID eventId,
    @JsonProperty(required = true) NotificationEventType eventType,
    @JsonProperty(required = true) Long userId,
    @JsonProperty(required = true) String email,
    @JsonProperty(required = true) NotificationPayload payload
) {}
