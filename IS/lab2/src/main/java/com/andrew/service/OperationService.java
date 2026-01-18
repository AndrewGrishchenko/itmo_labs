package com.andrew.service;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

import com.andrew.dto.coordinates.CoordinatesRequest;
import com.andrew.dto.coordinates.CoordinatesResponse;
import com.andrew.dto.import_history.BulkImportRequest;
import com.andrew.dto.import_history.ImportResult;
import com.andrew.dto.location.LocationRequest;
import com.andrew.dto.location.LocationResponse;
import com.andrew.dto.movie.MovieRaw;
import com.andrew.dto.movie.MovieResponse;
import com.andrew.dto.person.PersonRaw;
import com.andrew.dto.person.PersonResponse;
import com.andrew.model.OperationStatus;
import com.andrew.repository.OperationRepository;
import com.andrew.security.CurrentUser;
import com.andrew.util.ResponseMapper;
import com.andrew.websocket.WebSocketNotifier;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@ApplicationScoped
public class OperationService {
    @Inject
    OperationRepository operationRepository;

    @Inject
    WebSocketNotifier notifier;

    @Inject CoordinatesService coordinatesService;
    @Inject LocationService locationService;
    @Inject PersonService personService;
    @Inject MovieService movieService;

    @Inject
    ImportHistoryService importHistoryService;

    @Inject
    CurrentUser currentUser;

    @Inject
    Validator validator;

    private static Jsonb jsonb = JsonbBuilder.create();

    @Transactional
    public MovieResponse findMovieWithMinGenre() {
        return ResponseMapper.toResponse(operationRepository.findMovieWithMinGenre());
    }

    @Transactional
    public Long countMoviesByGoldenPalm(int count) {
        return operationRepository.countMoviesByGoldenPalm(count);
    }

    @Transactional
    public Long countMoviesGenreLessThan(String genre) {
        return operationRepository.countMoviesGenreLessThan(genre);
    }

    @Transactional
    public List<PersonResponse> findScreenwritersWithNoOscars() {
        return operationRepository.findScreenWritersWithNoOscars().stream()
            .map(ResponseMapper::toResponse)
            .toList();
    }

    @Transactional
    public Long redistributeOscars(String sourceGenre, String destGenre) {
        return operationRepository.redistributeOscars(sourceGenre, destGenre);
    }

    @Transactional(rollbackOn = RuntimeException.class)
    public ImportResult parseObjects(InputStream content) {
        try {
            BulkImportRequest request;

            request = jsonb.fromJson(content, BulkImportRequest.class);

            if(request == null)
                throw new JsonbException("file is empty");

            if(request.getCoordinates().isEmpty() && request.getLocations().isEmpty() &&
                request.getPersons().isEmpty() && request.getMovies().isEmpty())
                throw new JsonbException("no import data found");

            validateList(request.getCoordinates(), "coordinates");
            validateList(request.getLocations(), "location");
            validateList(request.getPersons(), "person");
            validateList(request.getMovies(), "movie");

            ImportResult result = new ImportResult();

            if (!request.getCoordinates().isEmpty()) {
                for (CoordinatesRequest dto : request.getCoordinates()) {
                    CoordinatesResponse response = coordinatesService.createCoordinates(dto);
                    result.addCoordinates(response);
                }
            }

            if (!request.getLocations().isEmpty()) {
                for (LocationRequest dto : request.getLocations()) {
                    LocationResponse response = locationService.createLocation(dto);
                    result.addLocation(response);
                }
            }

            if (!request.getPersons().isEmpty()) {
                for (PersonRaw dto : request.getPersons()) {
                    PersonResponse response = personService.createPerson(dto);
                    result.addPerson(response);
                }
            }

            if (!request.getMovies().isEmpty()) {
                for (MovieRaw dto : request.getMovies()) {
                    MovieResponse response = movieService.createMovie(dto);
                    result.addMovie(response);
                }
            }

            importHistoryService.createImportHistory(currentUser.getUser(), OperationStatus.OK, result.getTotalCount());
            return result;
        } catch (RuntimeException e) {
            importHistoryService.createImportHistory(currentUser.getUser(), OperationStatus.ERROR, 0);
            throw e;
        }
    }

    private <T> void validateList(List<T> list, String entityName) {
        for (int i = 0; i < list.size(); i++) {
            T dto = list.get(i);
            Set<ConstraintViolation<T>> violations = validator.validate(dto);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(
                    entityName + " validation error at index " + i + ": " + violations,
                    violations
                );
            }
        }
    }
}
