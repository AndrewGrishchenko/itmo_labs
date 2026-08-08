package com.andrew.repository;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;

import com.andrew.model.VisitRequest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class VisitRequestRepository {
    @Inject
    SessionFactory sessionFactory;

    public void save(VisitRequest visitRequest) {
        sessionFactory.getCurrentSession().persist(visitRequest);
    }

    public List<VisitRequest> getAll() {
        return sessionFactory.getCurrentSession().createQuery("from VisitRequest", VisitRequest.class).list();
    }

    public List<VisitRequest> getByUserId(Long id) {
        return sessionFactory.getCurrentSession().createQuery("from VisitRequest vr where vr.user.id = :id", VisitRequest.class)
            .setParameter("id", id)
            .list();
    }

    public Optional<VisitRequest> findById(Long id) {
        return sessionFactory.getCurrentSession().createQuery("from VisitRequest vr where vr.id = :id", VisitRequest.class)
            .setParameter("id", id)
            .uniqueResultOptional();
    }

    public VisitRequest update(VisitRequest visitRequest) {
        return sessionFactory.getCurrentSession().merge(visitRequest);
    }

    public void delete(VisitRequest visitRequest) {
        sessionFactory.getCurrentSession().remove(visitRequest);
    }
}
