package com.andrew.dto.location;

import com.andrew.dto.OwnerResponse;

public record LocationResponse (
    Long id,
    OwnerResponse owner,
    Double x,
    double y,
    String name
) {}
