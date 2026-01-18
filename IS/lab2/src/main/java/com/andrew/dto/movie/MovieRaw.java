package com.andrew.dto.movie;

import com.andrew.dto.coordinates.CoordinatesRequest;
import com.andrew.dto.person.PersonRaw;
import com.andrew.model.MovieGenre;
import com.andrew.model.MpaaRating;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MovieRaw(
    @NotNull @NotEmpty String name,
    @NotNull @Valid CoordinatesRequest coordinates,
    @NotNull @Min(0) long oscarsCount,
    @NotNull @Positive Float budget,
    @NotNull @Positive double totalBoxOffice,
    MpaaRating mpaaRating,
    PersonRaw director,
    PersonRaw screenwriter,
    @NotNull @Valid PersonRaw operator,
    @NotNull @Positive Long length,
    @NotNull @Positive int goldenPalmCount,
    MovieGenre genre
) {}
