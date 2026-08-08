package com.andrew.repository;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;

import com.andrew.model.Fine;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class FineRepository {
    @Inject
    SessionFactory sessionFactory;

    public void save(Fine fine) {
        sessionFactory.getCurrentSession().persist(fine);
    }

    public List<Fine> getAll() {
        return sessionFactory.getCurrentSession().createQuery("from Fine", Fine.class).list();
    }

    public Optional<Fine> findById(Long id) {
        return sessionFactory.getCurrentSession().createQuery("from Fine f where f.id = :id", Fine.class)
            .setParameter("id", id)
            .uniqueResultOptional();
    }

    public Optional<Fine> findByCaseId(Long id) {
        return sessionFactory.getCurrentSession().createQuery("from Fine f where f.caseValue.id = :id", Fine.class)
            .setParameter("id", id)
            .uniqueResultOptional();
    }

    public Fine update(Fine fine) {
        return sessionFactory.getCurrentSession().merge(fine);
    }

    public void delete(Fine fine) {
        sessionFactory.getCurrentSession().remove(fine);
    }
}
