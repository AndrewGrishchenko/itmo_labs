package com.andrew.dto.requirement;

import java.time.LocalDate;

import com.andrew.model.enums.RequirementStatus;
import com.andrew.model.enums.SupplyType;

public record RequirementResponseDTO(
    Long id,
    Long userId,
    SupplyType rewardType,
    Long rewardAmount,
    LocalDate startDate,
    LocalDate untilDate,
    RequirementStatus status
) {}
