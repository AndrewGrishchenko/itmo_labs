package com.andrew.repository;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;

import com.andrew.model.IncidentReport;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class IncidentReportRepository {
    @Inject
    SessionFactory sessionFactory;

    public void save(IncidentReport incidentReport) {
        sessionFactory.getCurrentSession().persist(incidentReport);
    }

    public List<IncidentReport> getAll() {
        return sessionFactory.getCurrentSession().createQuery("from IncidentReport", IncidentReport.class).list();
    }

    public Optional<IncidentReport> findById(Long id) {
        return sessionFactory.getCurrentSession().createQuery("from IncidentReport ir where ir.id = :id", IncidentReport.class)
            .setParameter("id", id)
            .uniqueResultOptional();
    }

    public IncidentReport update(IncidentReport incidentReport) {
        return sessionFactory.getCurrentSession().merge(incidentReport);
    }

    public void delete(IncidentReport incidentReport) {
        sessionFactory.getCurrentSession().remove(incidentReport);
    }
}
