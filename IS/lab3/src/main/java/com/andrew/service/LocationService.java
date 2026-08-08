package com.andrew.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.andrew.dto.PageResponse;
import com.andrew.dto.location.LocationFilter;
import com.andrew.dto.location.LocationRequest;
import com.andrew.dto.location.LocationResponse;
import com.andrew.exceptions.ConflictException;
import com.andrew.exceptions.NotFoundException;
import com.andrew.exceptions.ValidationException;
import com.andrew.model.Location;
import com.andrew.model.Person;
import com.andrew.repository.LocationRepository;
import com.andrew.repository.PersonRepository;
import com.andrew.security.CurrentUser;
import com.andrew.util.ObjectFactory;
import com.andrew.util.ResponseMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ForbiddenException;

@ApplicationScoped
public class LocationService {
    @Inject
    LocationRepository locationRepository;

    @Inject
    PersonRepository personRepository;

    @Inject
    CurrentUser currentUser;

    @Transactional
    public LocationResponse createLocation(LocationRequest dto) {
        return createLocation(ObjectFactory.createLocation(dto, currentUser.getUser()));
    }

    @Transactional
    private LocationResponse createLocation(Location location) {
        Optional<Location> existingLocation = locationRepository.findByXY(location.getX(), location.getY());
        if (existingLocation.isPresent()) {
            if (!existingLocation.get().getName().equals(location.getName())) {
                throw new ValidationException(
                    String.format("geographic collision: location \"%s\" is already located at (%s, %s) coordinates",
                        existingLocation.get().getName(), existingLocation.get().getX(), existingLocation.get().getY()
                    )
                );
            }

            return ResponseMapper.toResponse(existingLocation.get());
        }

        locationRepository.save(location);
        return ResponseMapper.toResponse(location);
    }

    @Transactional
    public LocationResponse getLocationById(Long id) {
        return ResponseMapper.toResponse(locationRepository.findById(id)
                          .orElseThrow(() -> new NotFoundException("Location with id " + id + " not found")));
    }

    @Transactional
    public PageResponse<LocationResponse> getAllLocations(boolean mine, int page, int size, String sort, String order, LocationFilter filter) {
        PageResponse<Location> pagedResponse = mine ?
            locationRepository.findAllByUserPaginatedAndSorted(currentUser.getUser(), page, size, sort, order, filter) :
            locationRepository.findAllPaginatedAndSorted(page, size, sort, order, filter);

        return new PageResponse<LocationResponse>(
            pagedResponse.content().stream()
                .map(ResponseMapper::toResponse)
                .collect(Collectors.toList()),
            pagedResponse.totalElements()
        );
    }

    @Transactional
    public LocationResponse updateLocation(Long id, LocationRequest dto) {
        Location existing = locationRepository.findById(id)
                                       .orElseThrow(() -> new NotFoundException("Location with id " + id + " not found"));
        
        if (!currentUser.isAdmin() && !existing.getOwner().getId().equals(currentUser.getUser().getId()))
            throw new ForbiddenException("No permission to update");
        
        existing.setX(dto.x());
        existing.setY(dto.y());
        existing.setName(dto.name());

        return ResponseMapper.toResponse(locationRepository.update(existing));
    }

    @Transactional
    public void deleteLocation(Long id) {
        Location location = locationRepository.findById(id)
                                       .orElseThrow(() -> new NotFoundException("Location with id " + id + " not found"));
        
        if (!currentUser.isAdmin() && !location.getOwner().getId().equals(currentUser.getUser().getId()))
            throw new ForbiddenException("No permission to delete");
        
        if (personRepository.existsByLocationAndNotOwner(location, currentUser.getUser()))
            throw new ConflictException("FOREIGN_DEPENDENCY_CONFLICT");

        List<Person> dependencies = personRepository.getAllWithLocation(location);
        
        if (!dependencies.isEmpty())
            throw new ConflictException("DEPENDENCY_CONFLICT", Map.of("Person", dependencies.size()));

        locationRepository.delete(location);
    }
}
