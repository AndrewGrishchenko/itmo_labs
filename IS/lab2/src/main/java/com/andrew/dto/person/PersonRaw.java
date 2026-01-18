package com.andrew.dto.person;

import com.andrew.dto.location.LocationRequest;
import com.andrew.model.Color;
import com.andrew.model.Country;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PersonRaw(
    @NotNull @NotEmpty String name,
    Color eyeColor,
    Color hairColor,
    @Valid LocationRequest location,
    @NotNull @Positive float weight,
    Country nationality
) {}
