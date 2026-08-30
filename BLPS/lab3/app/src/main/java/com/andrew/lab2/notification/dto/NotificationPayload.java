package com.andrew.lab2.notification.dto;

import com.andrew.lab2.entity.studs.Complaint;
import com.andrew.lab2.entity.studs2.Video;

public record NotificationPayload(
    NotificationComplaintInfo complaintInfo,
    NotificationVideoInfo videoInfo
) {
    public static NotificationPayload forBlockedContent(Complaint complaint, Video video) {
        return new NotificationPayload(
            new NotificationComplaintInfo(
                complaint.getId(),
                complaint.getClaimDetails(),
                complaint.getModeratorComment()
            ),
            new NotificationVideoInfo(
                video.getId(),
                video.getTitle()
            )
        );
    }

    public static NotificationPayload forComplaint(Complaint complaint) {
        return new NotificationPayload(
            new NotificationComplaintInfo(
                complaint.getId(),
                complaint.getClaimDetails(),
                complaint.getModeratorComment()
            ),
            null
        );
    }
}
