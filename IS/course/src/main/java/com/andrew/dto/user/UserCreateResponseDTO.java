package com.andrew.dto.user;

import com.andrew.model.Role;

public record UserCreateResponseDTO(
    Long id,
    String username,
    String fullname,
    Role role,
    String email
) {}
