package com.andrew.dto.person;

import com.andrew.model.Color;
import com.andrew.model.Country;
import com.andrew.model.Location;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PersonRequest(
    @NotNull @NotEmpty String name,
    Color eyeColor,
    Color hairColor,
    Location location,
    @NotNull @Positive float weight,
    Country nationality
) {}
