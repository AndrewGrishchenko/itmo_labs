package com.andrew.dto.location;

import com.andrew.dto.OwnerResponse;

public record LocationResponse (
    int id,
    OwnerResponse owner,
    Double x,
    double y,
    String name
) {}
