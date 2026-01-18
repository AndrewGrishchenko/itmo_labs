package com.andrew.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record RedistributeOscarRequest(
    @NotNull @NotEmpty String sourceGenre,
    @NotNull @NotEmpty String destGenre
) {}
