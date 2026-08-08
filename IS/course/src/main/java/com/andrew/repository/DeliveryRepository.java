package com.andrew.repository;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;

import com.andrew.model.Delivery;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DeliveryRepository {
    @Inject
    SessionFactory sessionFactory;

    public void save(Delivery delivery) {
        sessionFactory.getCurrentSession().persist(delivery);
    }

    public List<Delivery> getAll() {
        return sessionFactory.getCurrentSession().createQuery("from Delivery", Delivery.class).list();
    }

    public Optional<Delivery> findById(Long id) {
        return sessionFactory.getCurrentSession().createQuery("from Delivery d where d.id = :id", Delivery.class)
            .setParameter("id", id)
            .uniqueResultOptional();
    }

    public List<Delivery> findByVisitId(Long id) {
        return sessionFactory.getCurrentSession().createQuery("from Delivery d where d.visitRequest.id = :id", Delivery.class)
            .setParameter("id", id)
            .list();
    }

    public Delivery update(Delivery delivery) {
        return sessionFactory.getCurrentSession().merge(delivery);
    }

    public void delete(Delivery delivery) {
        sessionFactory.getCurrentSession().remove(delivery);
    }
}
