package com.andrew.dto.segment;

import jakarta.validation.constraints.NotNull;

public record SegmentCreateDTO(
    @NotNull Long routeId,
    @NotNull Long zoneId,
    @NotNull Long stepIndex
) {}
