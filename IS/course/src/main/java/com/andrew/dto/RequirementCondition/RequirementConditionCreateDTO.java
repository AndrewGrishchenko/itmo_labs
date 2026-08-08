package com.andrew.dto.RequirementCondition;

import com.andrew.model.ActionType;
import com.andrew.model.ShipType;

import jakarta.validation.constraints.NotNull;

public record RequirementConditionCreateDTO(
    @NotNull Long requirementId,
    @NotNull Long zoneId,
    @NotNull ShipType shipType,
    @NotNull ActionType actionType
) {}
