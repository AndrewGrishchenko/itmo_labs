package com.andrew.dto.RouteRequest;

import java.util.List;

import com.andrew.model.Goal;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record RouteRequestCreateDTO(
    @NotNull Long shipId,
    @NotNull Long sourceZoneId,
    @NotNull Long targetZoneId,
    @NotNull Goal goal,
    @NotNull @NotEmpty List<Long> documentsIds
) {}
