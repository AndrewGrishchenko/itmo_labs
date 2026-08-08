package com.andrew.dto.ship;

import com.andrew.model.ShipType;

public record ShipResponseDTO(
    Long id,
    Long captainId,
    ShipType shipType
) {}
