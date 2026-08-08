package com.andrew.dto.delivery;

import com.andrew.model.enums.SupplyType;

public record DeliveryResponseDTO(
    Long id,
    SupplyType supplyType,
    Long visitRequestId,
    Integer amount
) {}
