package com.andrew.dto.coordinates;

import com.andrew.dto.OwnerResponse;

public record CoordinatesResponse(
    Long id,
    OwnerResponse owner,
    long x,
    double y
) {}
