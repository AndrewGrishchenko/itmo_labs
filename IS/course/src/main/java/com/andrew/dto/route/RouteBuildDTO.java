package com.andrew.dto.route;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record RouteBuildDTO(
    @NotNull @NotEmpty List<Long> zoneIds
) {}
