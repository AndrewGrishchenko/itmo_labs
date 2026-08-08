package com.andrew.service;

import java.util.Optional;

import org.hibernate.SessionFactory;

import com.andrew.exceptions.ValidationException;
import com.andrew.interfaces.Identifiable;
import com.andrew.model.Coordinates;
import com.andrew.model.Location;
import com.andrew.model.Movie;
import com.andrew.model.Person;
import com.andrew.repository.CoordinatesRepository;
import com.andrew.repository.LocationRepository;
import com.andrew.repository.MovieRepository;
import com.andrew.repository.PersonRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class EntityResolver {
    @Inject CoordinatesRepository coordinatesRepository;
    @Inject LocationRepository locationRepository;
    @Inject PersonRepository personRepository;
    @Inject MovieRepository movieRepository;

    @Inject
    private SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    public <T extends Identifiable> T resolve(T entity) {
        if (entity == null) return null;

        if (entity.getId() == null) {
            sessionFactory.getCurrentSession().persist(entity);
            return entity;
        }
        
        Long id = entity.getId();
        Optional<?> found = Optional.empty();

        if (entity instanceof Coordinates) {
            found = coordinatesRepository.findById(id);
        } else if (entity instanceof Location) {
            found = locationRepository.findById(id);
        } else if (entity instanceof Person) {
            found = personRepository.findById(id);
        } else if (entity instanceof Movie) {
            found = movieRepository.findById(id);
        } else {
            throw new IllegalArgumentException("unknown entity instance: " + entity.getClass().getName());
        }

        return (T) found.orElseThrow(() -> new ValidationException(
            String.format("%s with id %d not found", entity.getClass().getSimpleName(), id)
        ));
    }
}
