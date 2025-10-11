package com.andrew.dao;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;

import com.andrew.model.Coordinates;
import com.andrew.model.User;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CoordinatesDao {
    @Inject
    Session session;

    public void save(Coordinates coordinates) {
        session.beginTransaction();
        session.persist(coordinates);
        session.getTransaction().commit();
    }

    public List<Coordinates> getAll() {
        return session.createQuery("from Coordinates", Coordinates.class).list();
    }

    public List<Coordinates> getAllUser(User user) {
        return session.createQuery("from Coordinates c where c.owner = :user", Coordinates.class)
                      .setParameter("user", user)
                      .list();
    }

    public Optional<Coordinates> findById(int id) {
        return session.createQuery("from Coordinates c where c.id = :id", Coordinates.class)
                      .setParameter("id", id)
                      .uniqueResultOptional();
    }

    public Coordinates update(Coordinates coordinates) {
        session.beginTransaction();
        Coordinates updated = session.merge(coordinates);
        session.getTransaction().commit();
        return updated;
    }

    public void delete(Coordinates coordinates) {
        session.beginTransaction();
        session.remove(coordinates);
        session.getTransaction().commit();
    }
}
