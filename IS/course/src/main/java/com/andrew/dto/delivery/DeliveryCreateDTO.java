package com.andrew.dto.delivery;

import com.andrew.model.enums.SupplyType;

import jakarta.validation.constraints.NotNull;

public record DeliveryCreateDTO(
    @NotNull SupplyType supplyType,
    @NotNull Long visitRequestId,
    @NotNull Integer amount
) {}
