package com.andrew.dto.coordinates;

import com.andrew.dto.OwnerResponse;

public record CoordinatesResponse(
    int id,
    OwnerResponse owner,
    long x,
    double y
) {}
