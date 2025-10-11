package com.andrew.dto.coordinates;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;

public record CoordinatesRequest (
    @NotNull long x,
    @NotNull @Max(450) double y
) {}