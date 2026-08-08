package com.andrew.dto.RequirementCondition;

import com.andrew.model.ActionType;
import com.andrew.model.ShipType;

public record RequirementConditionResponseDTO(
    Long id,
    Long zoneId,
    Long requirementId,
    ShipType shipType,
    ActionType actionType
) {}
