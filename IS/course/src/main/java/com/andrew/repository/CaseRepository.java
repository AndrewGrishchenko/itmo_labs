package com.andrew.repository;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;

import com.andrew.model.Case;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CaseRepository {
    @Inject
    SessionFactory sessionFactory;

    public void save(Case caseValue) {
        sessionFactory.getCurrentSession().persist(caseValue);
    }

    public List<Case> getAll() {
        return sessionFactory.getCurrentSession().createQuery("from Case", Case.class).list();
    }

    public Optional<Case> findById(Long id) {
        return sessionFactory.getCurrentSession().createQuery("from Case c where c.id = :id", Case.class)
            .setParameter("id", id)
            .uniqueResultOptional();
    }

    public Case update(Case caseValue) {
        return sessionFactory.getCurrentSession().merge(caseValue);
    }
    
    public void delete(Case caseValue) {
        sessionFactory.getCurrentSession().remove(caseValue);
    }
}
