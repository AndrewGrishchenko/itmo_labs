package com.andrew.dto.VisitRequest;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record VisitRequestCreateDTO(
    @NotNull Long userId,
    @NotNull LocalDate date
) {}
