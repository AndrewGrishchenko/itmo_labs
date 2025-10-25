package com.andrew.dto.person;

import com.andrew.dto.OwnerResponse;
import com.andrew.dto.location.LocationResponse;
import com.andrew.model.Color;
import com.andrew.model.Country;

public record PersonResponse(
    int id,
    OwnerResponse owner,
    String name,
    Color eyeColor,
    Color hairColor,
    LocationResponse location,
    float weight,
    Country nationality
) {}