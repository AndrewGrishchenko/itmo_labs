package com.andrew.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.andrew.dto.PageResponse;
import com.andrew.dto.coordinates.CoordinatesFilter;
import com.andrew.model.Coordinates;
import com.andrew.model.User;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CoordinatesDao {
    @Inject
    Session session;

    public void save(Coordinates coordinates) {
        session.beginTransaction();
        session.persist(coordinates);
        session.getTransaction().commit();
    }

    public List<Coordinates> getAll() {
        return session.createQuery("from Coordinates", Coordinates.class).list();
    }

    public List<Coordinates> getAllUser(User user) {
        return session.createQuery("from Coordinates c where c.owner = :user", Coordinates.class)
                      .setParameter("user", user)
                      .list();
    }

    public PageResponse<Coordinates> findAllPaginatedAndSorted(int page, int size, String sortField, String sortOrder, CoordinatesFilter filter) {
        Map<String, Object> parameters = new HashMap<>();
        String whereClause = buildWhereClause(filter, parameters, null);

        String countHql = "select count(c.id) from Coordinates c" + whereClause;
        Query<Long> countQuery = session.createQuery(countHql, Long.class);
        setFilterParameters(countQuery, parameters);
        long totalElements = countQuery.getSingleResult();

        if (totalElements == 0)
            return new PageResponse<>(List.of(), 0);

        String sanitizedSortField = sanitizeSortField(sortField);
        String sanitizedSortOrder = "desc".equalsIgnoreCase(sortOrder) ? "desc" : "asc";
        String hql = String.format("from Coordinates c %s order by c.%s %s", whereClause, sanitizedSortField, sanitizedSortOrder);

        Query<Coordinates> query = session.createQuery(hql, Coordinates.class);
        setFilterParameters(query, parameters);
        query.setFirstResult(page * size);
        query.setMaxResults(size);

        List<Coordinates> content = query.list();
        return new PageResponse<>(content, totalElements);
        
        // String countHql = "select count(c.id) from Coordinates c";
        // Query<Long> countQuery = session.createQuery(countHql, Long.class);
        // long totalElements = countQuery.getSingleResult();

        // if (totalElements == 0)
        //     return new PageResponse<>(List.of(), 0);

        // String sanitizedSortField = sanitizeSortField(sortField);
        // String sanitizedSortOrder = "desc".equalsIgnoreCase(sortOrder) ? "desc" : "asc";

        // StringBuilder hql = new StringBuilder("from Coordinates c order by c.");
        // hql.append(sanitizedSortField);
        // hql.append(" ");
        // hql.append(sanitizedSortOrder);

        // Query<Coordinates> query = session.createQuery(hql.toString(), Coordinates.class);
        // query.setFirstResult(page * size);
        // query.setMaxResults(size);

        // List<Coordinates> content = query.list();

        // return new PageResponse<>(content, totalElements);
    }

    public PageResponse<Coordinates> findAllByUserPaginatedAndSorted(User user, int page, int size, String sortField, String sortOrder, CoordinatesFilter filter) {
        Map<String, Object> parameters = new HashMap<>();
        String whereClause = buildWhereClause(filter, parameters, user);

        String countHql = "select count(c.id) from Coordinates c" + whereClause;
        Query<Long> countQuery = session.createQuery(countHql, Long.class);
        setFilterParameters(countQuery, parameters);
        long totalElements = countQuery.getSingleResult();

        if (totalElements == 0)
            return new PageResponse<>(List.of(), 0);

        String sanitizedSortField = sanitizeSortField(sortField);
        String sanitizedSortOrder = "desc".equalsIgnoreCase(sortOrder) ? "desc" : "asc";
        String hql = String.format("from Coordinates c %s order by c.%s %s", whereClause, sanitizedSortField, sanitizedSortOrder);

        Query<Coordinates> query = session.createQuery(hql, Coordinates.class);
        setFilterParameters(query, parameters);
        query.setFirstResult(page * size);
        query.setMaxResults(size);

        List<Coordinates> content = query.list();
        return new PageResponse<>(content, totalElements);
        
        // String countHql = "select count(c.id) from Coordinates c where c.owner = :user";
        // Query<Long> countQuery = session.createQuery(countHql, Long.class);
        // countQuery.setParameter("user", user);
        // long totalElements = countQuery.getSingleResult();

        // if (totalElements == 0)
        //     return new PageResponse<>(List.of(), 0);

        // String sanitizedSortField = sanitizeSortField(sortField);
        // String sanitizedSortOrder = "desc".equalsIgnoreCase(sortOrder) ? "desc" : "asc";

        // StringBuilder hql = new StringBuilder("from Coordinates c where c.owner = :user order by c.");
        // hql.append(sanitizedSortField);
        // hql.append(" ");
        // hql.append(sanitizedSortOrder);

        // Query<Coordinates> query = session.createQuery(hql.toString(), Coordinates.class);
        // query.setParameter("user", user);
        // query.setFirstResult(page * size);
        // query.setMaxResults(size);

        // List<Coordinates> content = query.list();

        // return new PageResponse<>(content, totalElements);
    }

    public Optional<Coordinates> findById(int id) {
        return session.createQuery("from Coordinates c where c.id = :id", Coordinates.class)
                      .setParameter("id", id)
                      .uniqueResultOptional();
    }

    public Coordinates update(Coordinates coordinates) {
        session.beginTransaction();
        Coordinates updated = session.merge(coordinates);
        session.getTransaction().commit();
        return updated;
    }

    public void delete(Coordinates coordinates) {
        session.beginTransaction();
        session.remove(coordinates);
        session.getTransaction().commit();
    }

    private String sanitizeSortField(String sortField) {
        Set<String> allowedSortFields = Set.of("id", "x", "y", "owner.username");

        if (sortField != null && allowedSortFields.contains(sortField))
            return sortField;

        return "id";
    }

    private String buildWhereClause (CoordinatesFilter filter, Map<String, Object> parameters, User user) {
        StringBuilder whereClause = new StringBuilder();

        if (user != null) {
            appendWhere(whereClause, " c.owner = :user ");
            parameters.put("user", user);
        }

        if (filter != null) {
            if (filter.ownerId() != null) {
                appendWhere(whereClause, " c.owner.id = :ownerId ");
                parameters.put("ownerId", filter.ownerId());
            }

            if (filter.x() != null) {
                appendWhere(whereClause, " c.x = :x ");
                parameters.put("x", filter.x());
            }

            if (filter.y() != null) {
                appendWhere(whereClause, " c.y = :y ");
                parameters.put("y", filter.y());
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
