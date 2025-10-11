package com.andrew.dao;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;

import com.andrew.model.Location;
import com.andrew.model.User;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class LocationDao {
    @Inject
    Session session;

    public void save(Location location) {
        session.beginTransaction();
        session.persist(location);
        session.getTransaction().commit();
    }

    public List<Location> getAll() {
        return session.createQuery("from Location", Location.class).list();
    }

    public List<Location> getAllUser(User user) {
        return session.createQuery("from Location l where l.owner = :user", Location.class)
                      .setParameter("user", user)
                      .list();
    }

    public Optional<Location> findById(int id) {
        return session.createQuery("from Location l where l.id = :id", Location.class)
                      .setParameter("id", id)
                      .uniqueResultOptional();
    }

    public Location update(Location location) {
        session.beginTransaction();
        Location updated = session.merge(location);
        session.getTransaction().commit();
        return updated;
    }

    public void delete(Location location) {
        session.beginTransaction();
        session.remove(location);
        session.getTransaction().commit();
    }
}
