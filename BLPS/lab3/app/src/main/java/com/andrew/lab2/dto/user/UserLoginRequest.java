package com.andrew.lab2.dto.user;

public record UserLoginRequest(
    String username,
    String password
) {}
