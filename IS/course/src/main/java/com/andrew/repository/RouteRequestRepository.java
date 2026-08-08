package com.andrew.repository;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;

import com.andrew.model.RouteRequest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RouteRequestRepository {
    @Inject
    SessionFactory sessionFactory;

    public void save(RouteRequest routeRequest) {
        sessionFactory.getCurrentSession().persist(routeRequest);
    }

    public List<RouteRequest> getAll() {
        return sessionFactory.getCurrentSession().createQuery("from RouteRequest", RouteRequest.class).list();
    }

    public List<RouteRequest> getByCaptainId(Long id) {
        return sessionFactory.getCurrentSession().createQuery("from RouteRequest rr where rr.ship.captain.id = :id", RouteRequest.class)
            .setParameter("id", id)
            .list();
    }

    public Optional<RouteRequest> findById(Long id) {
        return sessionFactory.getCurrentSession().createQuery("from RouteRequest rr where rr.id = :id", RouteRequest.class)
            .setParameter("id", id)
            .uniqueResultOptional();
    }

    public RouteRequest update(RouteRequest routeRequest) {
        return sessionFactory.getCurrentSession().merge(routeRequest);
    }

    public void delete(RouteRequest routeRequest) {
        sessionFactory.getCurrentSession().remove(routeRequest);
    }
}
