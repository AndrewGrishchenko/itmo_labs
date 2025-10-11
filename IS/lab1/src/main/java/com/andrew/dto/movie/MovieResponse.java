package com.andrew.dto.movie;

import com.andrew.dto.OwnerResponse;
import com.andrew.model.Coordinates;
import com.andrew.model.MovieGenre;
import com.andrew.model.MpaaRating;
import com.andrew.model.Person;

public record MovieResponse(
    int id,
    OwnerResponse owner,
    String name,
    Coordinates coordinates,
    long oscarsCount,
    Float budget,
    double totalBoxOffice,
    MpaaRating mpaaRating,
    Person director,
    Person screenwriter,
    Person operator,
    Long length,
    int goldenPalmCount,
    MovieGenre genre
) {}
