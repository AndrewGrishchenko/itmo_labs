package com.andrew.notification.email;

public record EmailMessage(
    String recipient,
    String subject,
    String body
) {}
