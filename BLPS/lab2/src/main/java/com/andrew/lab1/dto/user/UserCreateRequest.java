package com.andrew.lab1.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UserCreateRequest(
    @NotBlank(message = "Username must not be blank")
    String username,

    @NotBlank(message = "Password must not be blank")
    String password
) {}
