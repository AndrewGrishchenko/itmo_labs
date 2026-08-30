package com.andrew.notification.event.dto;

public record NotificationPayload(
    NotificationComplaintInfo complaintInfo,
    NotificationVideoInfo videoInfo
) {}
