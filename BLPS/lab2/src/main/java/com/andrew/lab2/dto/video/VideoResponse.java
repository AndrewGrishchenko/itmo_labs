package com.andrew.lab2.dto.video;

import java.util.List;

import com.andrew.lab2.entity.enums.VideoStatus;

public record VideoResponse(
    Long id,
    String title,
    Long authorId,
    VideoStatus status,
    List<String> music
) {}
