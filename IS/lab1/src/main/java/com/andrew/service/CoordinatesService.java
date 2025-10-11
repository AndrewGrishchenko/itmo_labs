package com.andrew.service;

import java.util.List;

import com.andrew.dao.CoordinatesDao;
import com.andrew.dto.coordinates.CoordinatesRequest;
import com.andrew.exceptions.NotFoundException;
import com.andrew.model.Coordinates;
import com.andrew.security.CurrentUser;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ForbiddenException;

@ApplicationScoped
public class CoordinatesService {
    @Inject
    CoordinatesDao coordinatesDao;

    @Inject
    CurrentUser currentUser;

    public Coordinates createCoordinates(CoordinatesRequest dto) {
        Coordinates coordinates = new Coordinates(dto.x(), dto.y());
        coordinates.setOwner(currentUser.getUser());
        
        coordinatesDao.save(coordinates);
        return coordinates;
    }

    public Coordinates getCoordinatesById(int id) {
        return coordinatesDao.findById(id)
                             .orElseThrow(() -> new NotFoundException("Coordinates with id " + id + " not found"));
    }

    public List<Coordinates> getAllCoordinates(boolean mine) {
        return mine ? coordinatesDao.getAllUser(currentUser.getUser()) : coordinatesDao.getAll();
    }

    public Coordinates updateCoordinates(int id, CoordinatesRequest dto) {
        Coordinates existing = coordinatesDao.findById(id)
                                             .orElseThrow(() -> new NotFoundException("Coordinates with id " + id + " not found"));

        if (!currentUser.isAdmin() && !existing.getOwner().getId().equals(currentUser.getUser().getId()))
            throw new ForbiddenException("No permission to update");
        
        existing.setX(dto.x());
        existing.setY(dto.y());
        
        return coordinatesDao.update(existing);
    }

    public void deleteCoordinates(int id) {
        Coordinates coordinates = coordinatesDao.findById(id)
                                                .orElseThrow(() -> new NotFoundException("Coordinates with id " + id + " not found"));
        
        if (!currentUser.isAdmin() && !coordinates.getOwner().getId().equals(currentUser.getUser().getId()))
            throw new ForbiddenException("No permission to delete");
        
        coordinatesDao.delete(coordinates);
    }
}
