package com.andrew.repository;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;

import com.andrew.model.Ship;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ShipRepository {
    @Inject
    SessionFactory sessionFactory;

    public void save(Ship ship) {
        sessionFactory.getCurrentSession().persist(ship);
    }

    public List<Ship> getAll() {
        return sessionFactory.getCurrentSession().createQuery("from Ship", Ship.class).list();
    }

    public List<Ship> getByCaptainId(Long id) {
        return sessionFactory.getCurrentSession().createQuery("from Ship s where s.captain.id = :id", Ship.class)
            .setParameter("id", id)
            .list();
    }

    public Optional<Ship> findById(Long id) {
        return sessionFactory.getCurrentSession().createQuery("from Ship s where s.id = :id", Ship.class)
            .setParameter("id", id)
            .uniqueResultOptional();
    }

    public Ship update(Ship ship) {
        return sessionFactory.getCurrentSession().merge(ship);
    }

    public void delete(Ship ship) {
        sessionFactory.getCurrentSession().remove(ship);
    }
}
