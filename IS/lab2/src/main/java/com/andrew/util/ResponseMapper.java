package com.andrew.util;

import java.time.format.DateTimeFormatter;

import com.andrew.dto.OwnerResponse;
import com.andrew.dto.coordinates.CoordinatesResponse;
import com.andrew.dto.import_history.ImportHistoryResponse;
import com.andrew.dto.location.LocationResponse;
import com.andrew.dto.movie.MovieResponse;
import com.andrew.dto.person.PersonResponse;
import com.andrew.model.Coordinates;
import com.andrew.model.ImportHistory;
import com.andrew.model.Location;
import com.andrew.model.Movie;
import com.andrew.model.Person;
import com.andrew.model.User;

public class ResponseMapper {
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public static OwnerResponse toResponse(User entity) {
        return new OwnerResponse(
            entity.getId(),
            entity.getUsername()
        );
    }
    
    public static CoordinatesResponse toResponse(Coordinates entity) {
        return new CoordinatesResponse(
            entity.getId(),
            toResponse(entity.getOwner()),
            entity.getX(),
            entity.getY()
        );
    }

    public static LocationResponse toResponse(Location entity) {
        return new LocationResponse(
            entity.getId(),
            toResponse(entity.getOwner()),
            entity.getX(),
            entity.getY(),
            entity.getName()
        );
    }

    public static PersonResponse toResponse(Person entity) {
        return new PersonResponse(
            entity.getId(),
            toResponse(entity.getOwner()),
            entity.getName(),
            entity.getEyeColor(),
            entity.getHairColor(),
            entity.getLocation() != null ? toResponse(entity.getLocation()) : null,
            entity.getWeight(),
            entity.getNationality()
        );
    }

    public static MovieResponse toResponse(Movie entity) {
        return new MovieResponse(
            entity.getId(),
            toResponse(entity.getOwner()),
            entity.getName(),
            toResponse(entity.getCoordinates()),
            dtf.format(entity.getCreationDate()),
            entity.getOscarsCount(),
            entity.getBudget(),
            entity.getTotalBoxOffice(),
            entity.getMpaaRating(),
            entity.getDirector() != null ? toResponse(entity.getDirector()) : null,
            entity.getScreenwriter() != null ? toResponse(entity.getScreenwriter()) : null,
            entity.getOperator() != null ? toResponse(entity.getOperator()) : null,
            entity.getLength(),
            entity.getGoldenPalmCount(),
            entity.getGenre()
        );
    }

    public static ImportHistoryResponse toResponse(ImportHistory entity) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        
        return new ImportHistoryResponse(
            entity.getId(),
            dtf.format(entity.getCreationDate()),
            toResponse(entity.getUser()),
            entity.getOperationStatus(),
            entity.getObjectCount()
        );
    }
}
