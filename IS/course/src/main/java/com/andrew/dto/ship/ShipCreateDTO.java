package com.andrew.dto.ship;

import com.andrew.model.ShipType;

import jakarta.validation.constraints.NotNull;

public record ShipCreateDTO(
    @NotNull Long captainId,
    @NotNull ShipType shipType
) {}
