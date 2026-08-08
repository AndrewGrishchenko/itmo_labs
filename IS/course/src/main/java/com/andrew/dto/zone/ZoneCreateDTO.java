package com.andrew.dto.zone;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ZoneCreateDTO(
    @NotNull @NotEmpty String name
) {}
