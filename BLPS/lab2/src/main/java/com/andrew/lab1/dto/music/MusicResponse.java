package com.andrew.lab1.dto.music;

public record MusicResponse(
    Long id,
    String name,
    Long rightsholderId
) {}
