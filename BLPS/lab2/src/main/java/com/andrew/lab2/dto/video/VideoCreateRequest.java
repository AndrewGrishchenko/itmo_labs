package com.andrew.lab2.dto.video;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

public record VideoCreateRequest(
    @NotBlank(message = "Title must not be blank")
    String title,
    
    List<String> music
) {}
