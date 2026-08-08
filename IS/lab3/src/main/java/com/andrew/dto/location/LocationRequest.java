package com.andrew.dto.location;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record LocationRequest (
    @NotNull Double x,
    @NotNull double y,
    @NotNull @NotEmpty String name
) {}
