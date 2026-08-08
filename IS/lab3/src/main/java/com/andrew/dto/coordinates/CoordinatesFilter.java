package com.andrew.dto.coordinates;

public record CoordinatesFilter (
    Long ownerId,
    Long id,
    Long x,
    Double y
) {}