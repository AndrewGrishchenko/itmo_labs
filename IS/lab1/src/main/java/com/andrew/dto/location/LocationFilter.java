package com.andrew.dto.location;

public record LocationFilter (
    Long ownerId,
    Long id,
    String name,
    Double x,
    Double y
) {}