package com.andrew.repository;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;

import com.andrew.model.RequirementCondition;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RequirementConditionRepository {
    @Inject
    SessionFactory sessionFactory;

    public void save(RequirementCondition requirementCondition) {
        sessionFactory.getCurrentSession().persist(requirementCondition);
    }

    public List<RequirementCondition> getAll() {
        return sessionFactory.getCurrentSession().createQuery("from RequirementCondition", RequirementCondition.class).list();
    }

    public List<RequirementCondition> getByRequirementId(Long id) {
        return sessionFactory.getCurrentSession().createQuery("from RequirementCondition rc where rc.requirement.id = :id", RequirementCondition.class)
            .setParameter("id", id)
            .list();
    }

    public Optional<RequirementCondition> findById(Long id) {
        return sessionFactory.getCurrentSession().createQuery("from RequirementCondition rc where rc.id = :id", RequirementCondition.class)
            .setParameter("id", id)
            .uniqueResultOptional();
    }

    public RequirementCondition update(RequirementCondition requirementCondition) {
        return sessionFactory.getCurrentSession().merge(requirementCondition);
    }

    public void delete(RequirementCondition requirementCondition) {
        sessionFactory.getCurrentSession().remove(requirementCondition);
    }
}
