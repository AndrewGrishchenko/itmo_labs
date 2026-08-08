package com.andrew.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.andrew.dto.PageResponse;
import com.andrew.dto.person.PersonFilter;
import com.andrew.dto.person.PersonRaw;
import com.andrew.dto.person.PersonRequest;
import com.andrew.dto.person.PersonResponse;
import com.andrew.exceptions.ConflictException;
import com.andrew.exceptions.NotFoundException;
import com.andrew.model.Movie;
import com.andrew.model.Person;
import com.andrew.repository.MovieRepository;
import com.andrew.repository.PersonRepository;
import com.andrew.security.CurrentUser;
import com.andrew.util.ObjectFactory;
import com.andrew.util.ResponseMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ForbiddenException;

@ApplicationScoped
public class PersonService {
    @Inject
    PersonRepository personRepository;

    @Inject
    MovieRepository movieRepository;

    @Inject
    CurrentUser currentUser;

    @Transactional
    public PersonResponse createPerson(PersonRequest dto) {
        return createPerson(ObjectFactory.createPerson(dto, currentUser.getUser()));
    }

    @Transactional
    public PersonResponse createPerson(PersonRaw dto) {
        return createPerson(ObjectFactory.createPerson(dto, currentUser.getUser()));
    }

    @Transactional
    private PersonResponse createPerson(Person person) {
        personRepository.save(person);
        return ResponseMapper.toResponse(person);
    }

    @Transactional
    public PersonResponse getPersonById(Long id) {
        return ResponseMapper.toResponse(personRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Person with id " + id + " not found")));
    }

    @Transactional
    public PageResponse<PersonResponse> getAllPersons(boolean mine, int page, int size, String sort, String order, PersonFilter filter) {
        PageResponse<Person> pagedResponse = mine ?
            personRepository.findAllByUserPaginatedAndSorted(currentUser.getUser(), page, size, sort, order, filter) :
            personRepository.findAllPaginatedAndSorted(page, size, sort, order, filter);

        return new PageResponse<PersonResponse>(
            pagedResponse.content().stream()
                .map(ResponseMapper::toResponse)
                .collect(Collectors.toList()),
            pagedResponse.totalElements()
        );
    }

    @Transactional
    public PersonResponse updatePerson(Long id, PersonRequest dto) {
        Person existing = personRepository.findById(id)
                                   .orElseThrow(() -> new NotFoundException("Person with id " + id + " not found"));

        if (!currentUser.isAdmin() && !existing.getOwner().getId().equals(currentUser.getUser().getId()))
            throw new ForbiddenException("No permission to update");
        
        existing.setName(dto.name());
        existing.setEyeColor(dto.eyeColor());
        existing.setHairColor(dto.hairColor());
        existing.setLocation(dto.location());
        existing.setWeight(dto.weight());
        existing.setNationality(dto.nationality());

        return ResponseMapper.toResponse(personRepository.update(existing));
    }

    @Transactional
    public void deletePerson(Long id) {
        Person person = personRepository.findById(id)
                                 .orElseThrow(() -> new NotFoundException("Person with id " + id + " not found"));
        
        if (!currentUser.isAdmin() && !person.getOwner().getId().equals(currentUser.getUser().getId()))
            throw new ForbiddenException("No permission to delete");

        if (movieRepository.existsByPersonAndNotOwner(person, currentUser.getUser()))
            throw new ConflictException("FOREIGN_DEPENDENCY_CONFLICT");

        List<Movie> dependencies = movieRepository.findByPerson(person);
        
        if (!dependencies.isEmpty())
            throw new ConflictException("DEPENDENCY_CONFLICT", Map.of("Movie", dependencies.size()));
        
        personRepository.delete(person);
    }
}
