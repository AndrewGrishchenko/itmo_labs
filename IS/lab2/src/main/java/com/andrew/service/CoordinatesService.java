package com.andrew.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.andrew.dto.PageResponse;
import com.andrew.dto.coordinates.CoordinatesFilter;
import com.andrew.dto.coordinates.CoordinatesRequest;
import com.andrew.dto.coordinates.CoordinatesResponse;
import com.andrew.exceptions.ConflictException;
import com.andrew.exceptions.NotFoundException;
import com.andrew.model.Coordinates;
import com.andrew.model.Movie;
import com.andrew.repository.CoordinatesRepository;
import com.andrew.repository.MovieRepository;
import com.andrew.security.CurrentUser;
import com.andrew.util.ObjectFactory;
import com.andrew.util.ResponseMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ForbiddenException;

@ApplicationScoped
public class CoordinatesService {
    @Inject
    CoordinatesRepository coordinatesRepository;

    @Inject
    MovieRepository movieRepository;

    @Inject
    CurrentUser currentUser;

    @Transactional
    public CoordinatesResponse createCoordinates(CoordinatesRequest dto) {
        return createCoordinates(ObjectFactory.createCoordinates(dto, currentUser.getUser()));
    }

    @Transactional
    private CoordinatesResponse createCoordinates(Coordinates coordinates) {
        coordinatesRepository.save(coordinates);
        return ResponseMapper.toResponse(coordinates);
    }

    @Transactional
    public CoordinatesResponse getCoordinatesById(Long id) {
        return ResponseMapper.toResponse(coordinatesRepository.findById(id)
                             .orElseThrow(() -> new NotFoundException("Coordinates with id " + id + " not found")));
    }

    @Transactional
    public PageResponse<CoordinatesResponse> getAllCoordinates(boolean mine, int page, int size, String sort, String order, CoordinatesFilter filter) {
        PageResponse<Coordinates> pagedResponse = mine ?
            coordinatesRepository.findAllByUserPaginatedAndSorted(currentUser.getUser(), page, size, sort, order, filter) :
            coordinatesRepository.findAllPaginatedAndSorted(page, size, sort, order, filter);

        return new PageResponse<CoordinatesResponse>(
            pagedResponse.content().stream()
                .map(ResponseMapper::toResponse)
                .collect(Collectors.toList()),
            pagedResponse.totalElements()
        );
    }

    @Transactional
    public CoordinatesResponse updateCoordinates(Long id, CoordinatesRequest dto) {
        Coordinates existing = coordinatesRepository.findById(id)
                                             .orElseThrow(() -> new NotFoundException("Coordinates with id " + id + " not found"));

        if (!currentUser.isAdmin() && !existing.getOwner().getId().equals(currentUser.getUser().getId()))
            throw new ForbiddenException("No permission to update");
        
        existing.setX(dto.x());
        existing.setY(dto.y());
        
        return ResponseMapper.toResponse(coordinatesRepository.update(existing));
    }

    @Transactional
    public void deleteCoordinates(Long id) {
        Coordinates coordinates = coordinatesRepository.findById(id)
                                                .orElseThrow(() -> new NotFoundException("Coordinates with id " + id + " not found"));
        
        if (!currentUser.isAdmin() && !coordinates.getOwner().getId().equals(currentUser.getUser().getId()))
            throw new ForbiddenException("No permission to delete");

        if (movieRepository.existsByCoordinateAndNotOwner(coordinates, currentUser.getUser()))
            throw new ConflictException("FOREIGN_DEPENDENCY_CONFLICT");
        
        List<Movie> dependencies = movieRepository.findByCoordinate(coordinates);

        if (!dependencies.isEmpty())
            throw new ConflictException("DEPENDENCY_CONFLICT", Map.of("Movie", dependencies.size()));
        
        coordinatesRepository.delete(coordinates);
    }
}
