package com.andrew.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

import org.hibernate.SessionFactory;

import com.andrew.dto.coordinates.CoordinatesRequest;
import com.andrew.dto.import_history.BulkImportRequest;
import com.andrew.dto.import_history.ImportHistoryResponse;
import com.andrew.dto.import_history.ImportResult;
import com.andrew.dto.location.LocationRequest;
import com.andrew.dto.movie.MovieRaw;
import com.andrew.dto.movie.MovieResponse;
import com.andrew.dto.person.PersonRaw;
import com.andrew.dto.person.PersonResponse;
import com.andrew.exceptions.MinioDownException;
import com.andrew.model.ImportHistory;
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
import jakarta.persistence.PersistenceException;
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

    @Inject MinioService minioService;

    @Inject
    ImportHistoryService importHistoryService;

    @Inject
    CurrentUser currentUser;

    @Inject
    Validator validator;

    @Inject
    SessionFactory sessionFactory;

    private static Jsonb jsonb = JsonbBuilder.create();

    private static boolean SIM_DB_FAILURE = false;

    public static void setFailure(boolean status) {
        SIM_DB_FAILURE = status;
    }

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
        byte[] bytes;
        try {
            bytes = content.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read input stream", e);
        }

        ImportHistory history = importHistoryService.createImportHistory(
            currentUser.getUser(), OperationStatus.PENDING, 0
        );

        BulkImportRequest request;
        try (InputStream validateStream = new ByteArrayInputStream(bytes)) {
            request = jsonb.fromJson(validateStream, BulkImportRequest.class);
            if (request == null || (
                    request.getCoordinates().isEmpty() &&
                    request.getLocations().isEmpty() &&
                    request.getPersons().isEmpty() &&
                    request.getMovies().isEmpty()
            )) {
                throw new JsonbException("file is empty or no import data found");
            }

            validateList(request.getCoordinates(), "coordinates");
            validateList(request.getLocations(), "location");
            validateList(request.getPersons(), "person");
            validateList(request.getMovies(), "movie");

        } catch (RuntimeException e) {
            importHistoryService.markError(history);
            throw e;
        } catch (Exception e) {
            importHistoryService.markError(history);
            throw new RuntimeException(e);
        }

        // PREPARE
        String tmpObjectName = "imports/tmp/import_" + history.getId() + ".json";
        String finalObjectName = "imports/import_" + history.getId() + ".json";

        try (InputStream uploadStream = new ByteArrayInputStream(bytes)) {
            minioService.uploadFile("uploads", tmpObjectName, uploadStream, bytes.length, "application/json");
        } catch (Exception e) {
            importHistoryService.markError(history);
            throw new MinioDownException("Failed to upload to MinIO");
        }

        ImportResult result = new ImportResult();

        try {
            if (SIM_DB_FAILURE)
                throw new PersistenceException("simulated error");

            for (CoordinatesRequest dto : request.getCoordinates()) result.addCoordinates(coordinatesService.createCoordinates(dto));
            for (LocationRequest dto : request.getLocations()) result.addLocation(locationService.createLocation(dto));
            for (PersonRaw dto : request.getPersons()) result.addPerson(personService.createPerson(dto));
            for (MovieRaw dto : request.getMovies()) result.addMovie(movieService.createMovie(dto));

            // COMMIT
            minioService.rename("uploads", tmpObjectName, finalObjectName);
            history.setObjectCount(result.getTotalCount());
            importHistoryService.markOk(history);

            return result;

        } catch (Exception e) {
            // Rollback
            importHistoryService.markError(history);
            minioService.deleteFile("uploads", tmpObjectName);
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

    @Transactional
    public InputStream downloadFile(int id) {
        ImportHistoryResponse history = importHistoryService.getById(id);
        
        String objectName = "imports/import_" + history.id() + ".json";

        return minioService.downloadFile("uploads", objectName);
    }
}
