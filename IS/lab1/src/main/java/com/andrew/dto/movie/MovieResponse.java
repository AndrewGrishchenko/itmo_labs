package com.andrew.dto.movie;

import com.andrew.dto.OwnerResponse;
import com.andrew.dto.coordinates.CoordinatesResponse;
import com.andrew.dto.person.PersonResponse;
import com.andrew.model.MovieGenre;
import com.andrew.model.MpaaRating;

public record MovieResponse(
    int id,
    OwnerResponse owner,
    String name,
    CoordinatesResponse coordinates,
    String creationDate,
    long oscarsCount,
    Float budget,
    double totalBoxOffice,
    MpaaRating mpaaRating,
    PersonResponse director,
    PersonResponse screenwriter,
    PersonResponse operator,
    Long length,
    int goldenPalmCount,
    MovieGenre genre
) {}
