package com.andrew.lab1.dto.music;

import com.andrew.lab1.dto.user.UserResponse;

public record MusicResponse(
    Long id,
    String name,
    UserResponse rightsholder
) {}
