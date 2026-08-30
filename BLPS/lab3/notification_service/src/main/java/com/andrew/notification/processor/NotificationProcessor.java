package com.andrew.notification.processor;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.andrew.notification.email.EmailService;
import com.andrew.notification.event.NotificationEvent;
import com.andrew.notification.exception.NotificationDeliveryException;
import com.andrew.notification.notification.Notification;
import com.andrew.notification.notification.NotificationService;

import jakarta.resource.ResourceException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationProcessor {
    private final NotificationService notificationService;
    private final EmailService emailService;

    public void process(NotificationEvent event) {
        Optional<Notification> maybeNotification = notificationService.getOrCreateForProcessing(event);

        if (maybeNotification.isEmpty())
            return;

        Notification notification = maybeNotification.get();

        notificationService.markProcessing(notification);

        try {
            emailService.send(
                event.email(),
                createSubject(event),
                createBody(event)
            );

            notificationService.markSent(notification);
        } catch (ResourceException e) {
            notificationService.markFailed(notification);

            throw new NotificationDeliveryException("failed to send notification", e);
        }
    }

    private String createSubject(NotificationEvent event) {
        return switch (event.eventType()) {
            case CONTENT_AUTO_BLOCKED ->
                "Content has been blocked";

            case CONTENT_SENT_TO_MODERATION ->
                "Content has been sent to moderation";

            case COMPLAINT_SATISFIED ->
                "Complaint satisfied";

            case COMPLAINT_REJECTED ->
                "Complaint rejected";
        };
    }

    private String createBody(NotificationEvent event) {
        return switch (event.eventType()) {
            case CONTENT_AUTO_BLOCKED ->
                """
                Due to complaint #%d, video #%d \"%s\" was AUTO blocked. As reported, \"%s\"
                """.formatted(
                    event.payload().complaintInfo().id(),
                    event.payload().videoInfo().id(),
                    event.payload().videoInfo().title(),
                    event.payload().complaintInfo().claimDetails()
                );

            case CONTENT_SENT_TO_MODERATION ->
                """
                Complaint #%d with reported \"%s\" was sent to moderation
                """.formatted(
                    event.payload().complaintInfo().id(),
                    event.payload().complaintInfo().claimDetails()
                );

            case COMPLAINT_SATISFIED ->
                """
                Due to complaint #%d, video #%d \"%s\" was blocked. As reported, \"%s\". Moderator conclusion: \"%s\"
                """.formatted(
                    event.payload().complaintInfo().id(),
                    event.payload().videoInfo().id(),
                    event.payload().videoInfo().title(),
                    event.payload().complaintInfo().claimDetails(),
                    event.payload().complaintInfo().moderatorComment()
                );

            case COMPLAINT_REJECTED ->
                """
                Complaint #%d with reported \"%s\" was rejected. Moderator conclusion: \"%s\"
                """.formatted(
                    event.payload().complaintInfo().id(),
                    event.payload().complaintInfo().claimDetails(),
                    event.payload().complaintInfo().moderatorComment()
                );
        };
    }
}
