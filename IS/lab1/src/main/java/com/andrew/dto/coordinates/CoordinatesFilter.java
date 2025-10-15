package com.andrew.dto.coordinates;

public record CoordinatesFilter (
    Long ownerId,
    Long x,
    Double y
) {}