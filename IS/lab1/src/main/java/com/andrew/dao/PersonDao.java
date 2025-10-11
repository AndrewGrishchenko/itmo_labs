package com.andrew.dao;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;

import com.andrew.model.Person;
import com.andrew.model.User;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PersonDao {
    @Inject
    Session session;

    public void save(Person person) {
        session.beginTransaction();
        session.persist(person);
        session.getTransaction().commit();
    }

    public List<Person> getAll() {
        return session.createQuery("from Person", Person.class).list();
    }

    public List<Person> getAllUser(User user) {
        return session.createQuery("from Person p where p.owner = :user", Person.class)
                      .setParameter("user", user)
                      .list();
    }

    public Optional<Person> findById(int id) {
        return session.createQuery("from Person p where p.id = :id", Person.class)
                      .setParameter("id", id)
                      .uniqueResultOptional();
    }

    public Person update(Person person) {
        session.beginTransaction();
        Person updated = session.merge(person);
        session.getTransaction().commit();
        return updated;
    }

    public void delete(Person person) {
        session.beginTransaction();
        session.remove(person);
        session.getTransaction().commit();
    }
}
