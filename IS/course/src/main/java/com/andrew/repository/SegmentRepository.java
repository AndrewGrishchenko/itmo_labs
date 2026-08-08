package com.andrew.repository;

import java.util.List;

import org.hibernate.SessionFactory;

import com.andrew.model.Segment;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class SegmentRepository {
    @Inject
    SessionFactory sessionFactory;

    public List<Segment> getByRouteId(Long id) {
        return sessionFactory.getCurrentSession().createQuery("from Segment s where s.route.id = :id", Segment.class)
            .setParameter("id", id)
            .list();
    }

    // public void save(Segment segment) {
    //     sessionFactory.getCurrentSession().persist(segment);
    // }

    // public List<Segment> getAll() {
    //     return sessionFactory.getCurrentSession().createQuery("from Segment", Segment.class).list();
    // }

    // public Optional<Segment> findById(Long id) {
    //     return sessionFactory.getCurrentSession().createQuery("from Segment s where s.id = :id", Segment.class)
    //         .setParameter("id", id)
    //         .uniqueResultOptional();
    // }

    // public Segment update(Segment segment) {
    //     return sessionFactory.getCurrentSession().merge(segment);
    // }

    // public void delete(Segment segment) {
    //     sessionFactory.getCurrentSession().remove(segment);
    // }
}
