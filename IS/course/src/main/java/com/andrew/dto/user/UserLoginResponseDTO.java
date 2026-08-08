package com.andrew.dto.user;

import com.andrew.model.Role;

public record UserLoginResponseDTO(
    Long id,
    String token,
    Role role
) {}
