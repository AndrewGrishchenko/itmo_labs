package com.andrew.dto.location;

public record LocationFilter (
    Long ownerId,
    String name,
    Double x,
    Double y
) {}