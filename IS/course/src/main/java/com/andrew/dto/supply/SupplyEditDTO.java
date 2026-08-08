package com.andrew.dto.supply;

import com.andrew.model.enums.SupplyType;

import jakarta.validation.constraints.NotNull;

public record SupplyEditDTO(
    @NotNull SupplyType name,
    @NotNull Integer amount
) {}
