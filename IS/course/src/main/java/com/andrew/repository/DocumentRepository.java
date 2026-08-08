package com.andrew.repository;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;

import com.andrew.model.Document;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DocumentRepository {
    @Inject
    SessionFactory sessionFactory;

    public void save(Document document) {
        sessionFactory.getCurrentSession().persist(document);
    }

    public List<Document> getAll() {
        return sessionFactory.getCurrentSession().createQuery("from Document", Document.class).list();
    }

    public List<Document> findByOwnerId(Long id) {
        return sessionFactory.getCurrentSession().createQuery("from Document d where d.owner.id = :id", Document.class)
            .setParameter("id", id)
            .list();
    }

    public Optional<Document> findById(Long id) {
        return sessionFactory.getCurrentSession().createQuery("from Document d where d.id = :id", Document.class)
            .setParameter("id", id)
            .uniqueResultOptional();
    }

    public Document update(Document document) {
        return sessionFactory.getCurrentSession().merge(document);
    }

    public void delete(Document document) {
        sessionFactory.getCurrentSession().remove(document);
    }
}
