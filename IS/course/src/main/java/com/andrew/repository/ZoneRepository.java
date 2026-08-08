package com.andrew.repository;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;

import com.andrew.model.Zone;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ZoneRepository {
    @Inject
    SessionFactory sessionFactory;

    public void save(Zone zone) {
        sessionFactory.getCurrentSession().persist(zone);
    }

    public List<Zone> getAll() {
        return sessionFactory.getCurrentSession().createQuery("from Zone", Zone.class).list();
    }

    public Optional<Zone> findById(Long id) {
        return sessionFactory.getCurrentSession().createQuery("from Zone z where z.id = :id", Zone.class)
            .setParameter("id", id)
            .uniqueResultOptional();
    }

    public Zone update(Zone zone) {
        return sessionFactory.getCurrentSession().merge(zone);
    }

    public void delete(Zone zone) {
        sessionFactory.getCurrentSession().remove(zone);
    }
}
