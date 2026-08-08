package com.andrew.dto.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UserLoginDTO(
    @NotNull @NotEmpty String username,
    @NotNull @NotEmpty String password
) {}
