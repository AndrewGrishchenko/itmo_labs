package com.andrew.service;

import java.util.List;

import com.andrew.dao.LocationDao;
import com.andrew.dto.location.LocationRequest;
import com.andrew.exceptions.NotFoundException;
import com.andrew.model.Location;
import com.andrew.security.CurrentUser;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ForbiddenException;

@ApplicationScoped
public class LocationService {
    @Inject
    LocationDao locationDao;

    @Inject
    CurrentUser currentUser;

    public Location createLocation(LocationRequest dto) {
        Location location = new Location(dto.x(), dto.y(), dto.name());
        location.setOwner(currentUser.getUser());

        locationDao.save(location);
        return location;
    }

    public Location getLocationById(int id) {
        return locationDao.findById(id)
                          .orElseThrow(() -> new NotFoundException("Location with id " + id + " not found"));
    }

    public List<Location> getAllLocations(boolean mine) {
        return mine ? locationDao.getAllUser(currentUser.getUser()) : locationDao.getAll();
    }

    public Location updateLocation(int id, LocationRequest dto) {
        Location existing = locationDao.findById(id)
                                       .orElseThrow(() -> new NotFoundException("Location with id " + id + " not found"));
        
        if (!currentUser.isAdmin() && !existing.getOwner().getId().equals(currentUser.getUser().getId()))
            throw new ForbiddenException("No permission to update");
        
        existing.setX(dto.x());
        existing.setY(dto.y());
        existing.setName(dto.name());

        return locationDao.update(existing);
    }

    public void deleteLocation(int id) {
        Location location = locationDao.findById(id)
                                       .orElseThrow(() -> new NotFoundException("Location with id " + id + " not found"));
        
        if (!currentUser.isAdmin() && !location.getOwner().getId().equals(currentUser.getUser().getId()))
            throw new ForbiddenException("No permission to delete");
        
        locationDao.delete(location);
    }
}
