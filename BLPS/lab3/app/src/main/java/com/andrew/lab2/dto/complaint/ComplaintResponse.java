package com.andrew.lab2.dto.complaint;

import com.andrew.lab2.entity.enums.ComplaintStatus;

public record ComplaintResponse (
    Long id,
    Long videoId,
    Long rightsholderId,
    String claimDetails,
    ComplaintStatus status,
    String moderatorComment
) {}
