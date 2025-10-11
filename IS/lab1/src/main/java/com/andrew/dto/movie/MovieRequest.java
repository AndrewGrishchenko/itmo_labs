package com.andrew.dto.movie;

import com.andrew.model.Coordinates;
import com.andrew.model.MovieGenre;
import com.andrew.model.MpaaRating;
import com.andrew.model.Person;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MovieRequest(
    @NotNull @NotEmpty String name,
    @NotNull Coordinates coordinates,
    @NotNull @Positive long oscarsCount,
    @NotNull @Positive Float budget,
    @NotNull @Positive double totalBoxOffice,
    MpaaRating mpaaRating,
    Person director,
    Person screenwriter,
    @NotNull Person operator,
    @NotNull @Positive Long length,
    @NotNull @Positive int goldenPalmCount,
    MovieGenre genre
) {}
