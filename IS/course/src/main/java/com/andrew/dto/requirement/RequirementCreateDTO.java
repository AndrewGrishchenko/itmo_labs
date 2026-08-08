package com.andrew.dto.requirement;

import java.time.LocalDate;

import com.andrew.model.enums.SupplyType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RequirementCreateDTO(
    @NotNull SupplyType rewardType,
    @NotNull @Min(1) Long rewardAmount,
    @NotNull LocalDate startDate,
    @NotNull LocalDate untilDate
) {}
