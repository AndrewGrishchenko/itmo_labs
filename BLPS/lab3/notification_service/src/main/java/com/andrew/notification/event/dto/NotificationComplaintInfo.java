package com.andrew.notification.event.dto;

public record NotificationComplaintInfo(
    Long id,
    String claimDetails,
    String moderatorComment
) {}
