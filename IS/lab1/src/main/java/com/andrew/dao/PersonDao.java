package com.andrew.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.andrew.dto.PageResponse;
import com.andrew.dto.person.PersonFilter;
import com.andrew.model.Location;
import com.andrew.model.Person;
import com.andrew.model.User;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PersonDao {
    @Inject
    Session session;

    public void save(Person person) {
        session.beginTransaction();
        session.persist(person);
        session.getTransaction().commit();
    }

    public List<Person> getAll() {
        return session.createQuery("from Person", Person.class).list();
    }

    public List<Person> getAllUser(User user) {
        return session.createQuery("from Person p where p.owner = :user", Person.class)
                      .setParameter("user", user)
                      .list();
    }

    public PageResponse<Person> findAllPaginatedAndSorted(int page, int size, String sortField, String sortOrder, PersonFilter filter) {
        return findInternal(null, page, size, sortField, sortOrder, filter);
    }

    public PageResponse<Person> findAllByUserPaginatedAndSorted(User user, int page, int size, String sortField, String sortOrder, PersonFilter filter) {
        return findInternal(user, page, size, sortField, sortOrder, filter);
    }

    private PageResponse<Person> findInternal(User user, int page, int size, String sortField, String sortOrder, PersonFilter filter) {
        Map<String, Object> parameters = new HashMap<>();
        String whereClause = buildWhereClause(filter, parameters, user);

        String countHql = "select count(p.id) from Person p" + whereClause;
        Query<Long> countQuery = session.createQuery(countHql, Long.class);
        setFilterParameters(countQuery, parameters);
        long totalElements = countQuery.getSingleResult();

        if (totalElements == 0)
            return new PageResponse<>(List.of(), 0);

        String sanitizedSortField = sanitizeSortField(sortField);
        String sanitizedSortOrder = "desc".equalsIgnoreCase(sortOrder) ? "desc" : "asc";
        String hql = String.format("from Person p %s order by p.%s %s", whereClause, sanitizedSortField, sanitizedSortOrder);

        Query<Person> query = session.createQuery(hql, Person.class);
        setFilterParameters(query, parameters);
        query.setFirstResult(page * size);
        query.setMaxResults(size);

        List<Person> content = query.list();
        return new PageResponse<>(content, totalElements);
    }

    public Optional<Person> findById(int id) {
        return session.createQuery("from Person p where p.id = :id", Person.class)
                      .setParameter("id", id)
                      .uniqueResultOptional();
    }

    public List<Person> getAllWithLocation(Location location) {
        return session.createQuery("from Person p where p.location = :location", Person.class)
                      .setParameter("location", location)
                      .list();
    }

    public boolean existsByLocationAndNotOwner(Location location, User owner) {
        return session.createQuery("select count(p.id) from Person p where " +
                                   "p.location = :location and " +
                                   "p.owner != :owner", Long.class)
                      .setParameter("location", location)
                      .setParameter("owner", owner)
                      .getSingleResult() > 0;
    }

    public Person update(Person person) {
        session.beginTransaction();
        Person updated = session.merge(person);
        session.getTransaction().commit();
        return updated;
    }

    public void delete(Person person) {
        session.beginTransaction();
        session.remove(person);
        session.getTransaction().commit();
    }

    private String sanitizeSortField(String sortField) {
        Set<String> allowedSortFields = Set.of("id", "name", "eyeColor", "hairColor", "weight", "nationality", "owner.username");

        if (sortField != null && allowedSortFields.contains(sortField))
            return sortField;

        return "id";
    }

    private String buildWhereClause (PersonFilter filter, Map<String, Object> parameters, User user) {
        StringBuilder whereClause = new StringBuilder();

        if (user != null) {
            appendWhere(whereClause, " p.owner = :user ");
            parameters.put("user", user);
        }

        if (filter != null) {
            if (filter.ownerId() != null) {
                appendWhere(whereClause, " p.owner.id = :ownerId ");
                parameters.put("ownerId", filter.ownerId());
            }

            if (filter.id() != null) {
                appendWhere(whereClause, " p.id = :id ");
                parameters.put("id", filter.id());
            }

            if (filter.name() != null && !filter.name().isEmpty()) {
                appendWhere(whereClause, " p.name = :name ");
                parameters.put("name", filter.name());
            }

            if (filter.eyeColor() != null) {
                appendWhere(whereClause, " p.eyeColor = :eyeColor ");
                parameters.put("eyeColor", filter.eyeColor());
            }

            if (filter.hairColor() != null) {
                appendWhere(whereClause, " p.hairColor = :hairColor ");
                parameters.put("hairColor", filter.hairColor());
            }

            if (filter.locationId() != null) {
                appendWhere(whereClause, " p.location.id = :locationId ");
                parameters.put("locationId", filter.locationId().toString());
            }

            if (filter.weight() != null) {
                appendWhere(whereClause, " p.weight = :weight ");
                parameters.put("weight", filter.weight().toString());
            }

            if (filter.nationality() != null) {
                appendWhere(whereClause, " p.nationality = :nationality ");
                parameters.put("nationality", filter.nationality());
            }
        }

        return whereClause.toString();
    }

    private void setFilterParameters(Query<?> query, Map<String, Object> parameters) {
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
    }

    private void appendWhere(StringBuilder sb, String condition) {
        if (sb.length() == 0)
            sb.append(" where ");
        else
            sb.append(" and ");
        sb.append(condition);
    }
}
