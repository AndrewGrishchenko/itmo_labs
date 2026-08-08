package com.andrew.lab1.dto.complaint;

import com.andrew.lab1.dto.user.UserResponse;
import com.andrew.lab1.dto.video.VideoResponse;
import com.andrew.lab1.entity.enums.ComplaintStatus;

public record ComplaintResponse (
    Long id,
    VideoResponse videoId,
    UserResponse rightsholder,
    String claimDetails,
    ComplaintStatus status,
    String moderatorComment
) {}
