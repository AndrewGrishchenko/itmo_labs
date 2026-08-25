package com.andrew.lab2.dto.music;

public record MusicResponse(
    Long id,
    String name,
    Long rightsholderId
) {}
