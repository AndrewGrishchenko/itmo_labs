package com.andrew.util;

import com.andrew.model.Coordinates;
import com.andrew.model.Location;
import com.andrew.model.Movie;
import com.andrew.model.Person;
import com.andrew.model.User;
import com.andrew.dto.coordinates.CoordinatesRequest;
import com.andrew.dto.location.LocationRequest;
import com.andrew.dto.movie.MovieRaw;
import com.andrew.dto.movie.MovieRequest;
import com.andrew.dto.person.PersonRaw;
import com.andrew.dto.person.PersonRequest;

public class ObjectFactory {
    public static Coordinates createCoordinates(CoordinatesRequest dto, User owner) {
        if (dto == null) return null;
        
        Coordinates coordinates = new Coordinates(
            dto.x(),
            dto.y()
        );
        coordinates.setOwner(owner);
        return coordinates;
    }

    public static Location createLocation(LocationRequest dto, User owner) {
        if (dto == null) return null;
        
        Location location = new Location(
            dto.x(),
            dto.y(),
            dto.name()
        );
        location.setOwner(owner);
        return location;
    }

    public static Person createPerson(PersonRequest dto, User owner) {
        if (dto == null) return null;
        
        Person person = new Person(
            dto.name(),
            dto.eyeColor(),
            dto.hairColor(),
            dto.location(),
            dto.weight(),
            dto.nationality()
        );
        person.setOwner(owner);
        return person;
    }

    public static Person createPerson(PersonRaw dto, User owner) {
        if (dto == null) return null;

        Person person = new Person(
            dto.name(),
            dto.eyeColor(),
            dto.hairColor(),
            createLocation(dto.location(), owner),
            dto.weight(),
            dto.nationality()
        );
        person.setOwner(owner);
        return person;
    }

    public static Movie createMovie(MovieRequest dto, User owner) {
        if (dto == null) return null;

        Movie movie = new Movie(
            dto.name(),
            dto.coordinates(),
            dto.oscarsCount(),
            dto.budget(),
            dto.totalBoxOffice(),
            dto.mpaaRating(),
            dto.director(),
            dto.screenwriter(),
            dto.operator(),
            dto.length(),
            dto.goldenPalmCount(),
            dto.genre()
        );
        movie.setOwner(owner);
        return movie;
    }

    public static Movie createMovie(MovieRaw dto, User owner) {
        if (dto == null) return null;

        Movie movie = new Movie(
            dto.name(),
            createCoordinates(dto.coordinates(), owner),
            dto.oscarsCount(),
            dto.budget(),
            dto.totalBoxOffice(),
            dto.mpaaRating(),
            createPerson(dto.director(), owner),
            createPerson(dto.screenwriter(), owner),
            createPerson(dto.operator(), owner),
            dto.length(),
            dto.goldenPalmCount(),
            dto.genre()
        );
        movie.setOwner(owner);
        return movie;
    }
}
