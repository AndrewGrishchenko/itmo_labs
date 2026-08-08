package com.andrew.dto.user;

import com.andrew.model.Role;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UserUpdateDTO(
    @NotNull @NotEmpty String username,
    @NotNull @NotEmpty String fullname,
    @NotNull Role role,
    @NotNull @NotEmpty String email
) {}
