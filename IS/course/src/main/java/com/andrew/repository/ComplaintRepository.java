package com.andrew.repository;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;

import com.andrew.model.Complaint;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ComplaintRepository {
    @Inject
    SessionFactory sessionFactory;

    public void save(Complaint complaint) {
        sessionFactory.getCurrentSession().persist(complaint);
    }

    public List<Complaint> getAll() {
        return sessionFactory.getCurrentSession().createQuery("from Complaint", Complaint.class).list();
    }

    public List<Complaint> getByCaptainId(Long id) {
        return sessionFactory.getCurrentSession().createQuery("from Complaint c where c.routeRequest.ship.captain.id = :id", Complaint.class)
            .setParameter("id", id)
            .list();
    }

    public Optional<Complaint> findById(Long id) {
        return sessionFactory.getCurrentSession().createQuery("from Complaint c where c.id = :id", Complaint.class)
            .setParameter("id", id)
            .uniqueResultOptional();
    }

    public Complaint update(Complaint complaint) {
        return sessionFactory.getCurrentSession().merge(complaint);
    }

    public void delete(Complaint complaint) {
        sessionFactory.getCurrentSession().remove(complaint);
    }
}
