package com.andrew.lab2.dto.user;

import com.andrew.lab2.entity.enums.Role;

public record UserLoginResponse(
    String token,
    Role role
) {}
