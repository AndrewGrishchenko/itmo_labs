package com.andrew.repository;

import java.sql.Array;
import java.sql.CallableStatement;
import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.andrew.model.Route;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RouteRepository {
    @Inject
    SessionFactory sessionFactory;

    public void save(Route route) {
        sessionFactory.getCurrentSession().persist(route);
    }

    public List<Route> getAll() {
        return sessionFactory.getCurrentSession().createQuery("from Route", Route.class).list();
    }

    public List<Route> getByCaptainId(Long id) {
        return sessionFactory.getCurrentSession().createQuery("from Route r where r.routeRequest.ship.captain.id = :id", Route.class)
            .setParameter("id", id)
            .list();
    }

    public Optional<Route> findById(Long id) {
        return sessionFactory.getCurrentSession().createQuery("from Route r where r.id = :id", Route.class)
            .setParameter("id", id)
            .uniqueResultOptional();
    }

    public Optional<Route> findByRequestId(Long id) {
        return sessionFactory.getCurrentSession().createQuery("from Route r where r.routeRequest.id = :id", Route.class)
            .setParameter("id", id)
            .uniqueResultOptional();
    }

    public void buildRoute(Long routeId, List<Long> zoneIds) {
        Session session = sessionFactory.getCurrentSession();

        session.doWork(connection -> {
            try (CallableStatement stmt = connection.prepareCall("CALL proc_build_route_segments(?, ?)")) {
                stmt.setLong(1, routeId);

                Array sqlArray = connection.createArrayOf(
                    "bigint",
                    zoneIds.toArray(new Long[0])
                );

                stmt.setArray(2, sqlArray);

                stmt.execute();
            }
        });
    }

    public Route update(Route route) {
        return sessionFactory.getCurrentSession().merge(route);
    }

    public void delete(Route route) {
        sessionFactory.getCurrentSession().remove(route);
    }

    public boolean existsByRequestId(Long requestId) {
        return sessionFactory.getCurrentSession().createQuery("select count(r) from Route r where r.routeRequest.id = :id", Long.class)
            .setParameter("id", requestId)
            .getSingleResult() > 0;
    }
}
