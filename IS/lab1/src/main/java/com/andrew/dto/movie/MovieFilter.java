package com.andrew.dto.movie;

import com.andrew.model.MovieGenre;
import com.andrew.model.MpaaRating;

public record MovieFilter(
    Long ownerId,
    Long id,
    String name,
    Long coordinatesId,
    String creationDate,
    Long oscarsCount,
    Float budget,
    Double totalBoxOffice,
    MpaaRating mpaaRating,
    Long directorId,
    Long screenwriterId,
    Long operatorId,
    Long length,
    Integer goldenPalmCount,
    MovieGenre genre
) {}
