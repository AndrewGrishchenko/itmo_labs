package com.andrew.lab1.dto.user;

public record UserLoginRequest(
    String username,
    String password
) {}
