package com.andrew.dto.person;

import com.andrew.dto.OwnerResponse;
import com.andrew.model.Color;
import com.andrew.model.Country;
import com.andrew.model.Location;

public record PersonResponse(
    int id,
    OwnerResponse owner,
    String name,
    Color eyeColor,
    Color hairColor,
    Location location,
    float weight,
    Country nationality
) {}
