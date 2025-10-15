package com.andrew.dto.person;

import com.andrew.model.Color;
import com.andrew.model.Country;

public record PersonFilter (
    Long ownerId,
    String name,
    Color eyeColor,
    Color hairColor,
    Long locationId,
    Float weight,
    Country nationality
) {}
