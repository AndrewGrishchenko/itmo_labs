package com.andrew.lab2.notification.dto;

public record NotificationComplaintInfo(
    Long id,
    String claimDetails,
    String moderatorComment
) {}
