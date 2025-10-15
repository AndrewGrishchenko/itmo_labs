package com.andrew.service;

import com.andrew.dao.PersonDao;
import com.andrew.dto.PageResponse;
import com.andrew.dto.person.PersonFilter;
import com.andrew.dto.person.PersonRequest;
import com.andrew.exceptions.NotFoundException;
import com.andrew.model.Person;
import com.andrew.security.CurrentUser;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ForbiddenException;

@ApplicationScoped
public class PersonService {
    @Inject
    PersonDao personDao;

    @Inject
    CurrentUser currentUser;

    public Person createPerson(PersonRequest dto) {
        Person person = new Person(
            dto.name(),
            dto.eyeColor(),
            dto.hairColor(),
            dto.location(),
            dto.weight(),
            dto.nationality()
        );
        person.setOwner(currentUser.getUser());

        personDao.save(person);
        return person;
    }

    public Person getPersonById(int id) {
        return personDao.findById(id)
                        .orElseThrow(() -> new NotFoundException("Person with id " + id + " not found"));
    }

    public PageResponse<Person> getAllPersons(boolean mine, int page, int size, String sort, String order, PersonFilter filter) {
        return mine ?
            personDao.findAllByUserPaginatedAndSorted(currentUser.getUser(), page, size, sort, order, filter) :
            personDao.findAllPaginatedAndSorted(page, size, sort, order, filter);
    }

    public Person updatePerson(int id, PersonRequest dto) {
        Person existing = personDao.findById(id)
                                   .orElseThrow(() -> new NotFoundException("Person with id " + id + " not found"));

        if (!currentUser.isAdmin() && !existing.getOwner().getId().equals(currentUser.getUser().getId()))
            throw new ForbiddenException("No permission to update");
        
        existing.setName(dto.name());
        existing.setEyeColor(dto.eyeColor());
        existing.setHairColor(dto.hairColor());
        existing.setLocation(dto.location());
        existing.setWeight(dto.weight());
        existing.setNationality(dto.nationality());

        return personDao.update(existing);
    }

    public void deletePerson(int id) {
        Person person = personDao.findById(id)
                                 .orElseThrow(() -> new NotFoundException("Person with id " + id + " not found"));
        
        if (!currentUser.isAdmin() && !person.getOwner().getId().equals(currentUser.getUser().getId()))
            throw new ForbiddenException("No permission to delete");
        
        personDao.delete(person);
    }
}
