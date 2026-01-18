package com.andrew.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import com.andrew.dto.PageResponse;
import com.andrew.dto.location.LocationFilter;
import com.andrew.model.Location;
import com.andrew.model.User;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class LocationRepository {
    @Inject
    SessionFactory sessionFactory;

    public void save(Location location) {
        sessionFactory.getCurrentSession().persist(location);
    }

    public List<Location> getAll() {
        return sessionFactory.getCurrentSession().createQuery("from Location", Location.class).list();
    }

    public List<Location> getAllUser(User user) {
        return sessionFactory.getCurrentSession().createQuery("from Location l where l.owner = :user", Location.class)
                      .setParameter("user", user)
                      .list();
    }

    public PageResponse<Location> findAllPaginatedAndSorted(int page, int size, String sortField, String sortOrder, LocationFilter filter) {
        return findInternal(null, page, size, sortField, sortOrder, filter);
    }

    public PageResponse<Location> findAllByUserPaginatedAndSorted(User user, int page, int size, String sortField, String sortOrder, LocationFilter filter) {
        return findInternal(user, page, size, sortField, sortOrder, filter);
    }

    private PageResponse<Location> findInternal(User user, int page, int size, String sortField, String sortOrder, LocationFilter filter) {
        Map<String, Object> parameters = new HashMap<>();
        String whereClause = buildWhereClause(filter, parameters, user);

        String countHql = "select count(l.id) from Location l" + whereClause;
        Query<Long> countQuery = sessionFactory.getCurrentSession().createQuery(countHql, Long.class);
        setFilterParameters(countQuery, parameters);
        long totalElements = countQuery.getSingleResult();

        if (totalElements == 0)
            return new PageResponse<>(List.of(), 0);

        String sanitizedSortField = sanitizeSortField(sortField);
        String sanitizedSortOrder = "desc".equalsIgnoreCase(sortOrder) ? "desc" : "asc";
        String hql = String.format("from Location l %s order by l.%s %s", whereClause, sanitizedSortField, sanitizedSortOrder);

        Query<Location> query = sessionFactory.getCurrentSession().createQuery(hql, Location.class);
        setFilterParameters(query, parameters);
        query.setFirstResult(page * size);
        query.setMaxResults(size);

        List<Location> content = query.list();
        return new PageResponse<>(content, totalElements);
    }

    public Optional<Location> findById(Long id) {
        return sessionFactory.getCurrentSession().createQuery("from Location l where l.id = :id", Location.class)
                      .setParameter("id", id)
                      .uniqueResultOptional();
    }

    public Location update(Location location) {
        Location updated = sessionFactory.getCurrentSession().merge(location);
        return updated;
    }

    public Optional<Location> findByXY(Double x, double y) {
        return sessionFactory.getCurrentSession().createQuery("select l from Location l where " +
                                    "l.x = :x and " +
                                    "l.y = :y", Location.class)
                        .setParameter("x", x)
                        .setParameter("y", y)
                        .uniqueResultOptional();
    }

    public void delete(Location location) {
        sessionFactory.getCurrentSession().remove(location);
    }

    private String sanitizeSortField(String sortField) {
        Set<String> allowedSortFields = Set.of("id", "x", "y", "name", "owner.username");

        if (sortField != null && allowedSortFields.contains(sortField))
            return sortField;

        return "id";
    }

    private String buildWhereClause (LocationFilter filter, Map<String, Object> parameters, User user) {
        StringBuilder whereClause = new StringBuilder();

        if (user != null) {
            appendWhere(whereClause, " l.owner = :user ");
            parameters.put("user", user);
        }

        if (filter != null) {
            if (filter.ownerId() != null) {
                appendWhere(whereClause, " l.owner.id = :ownerId ");
                parameters.put("ownerId", filter.ownerId());
            }

            if (filter.id() != null) {
                appendWhere(whereClause, " l.id = :id ");
                parameters.put("id", filter.id());
            }

            if (filter.name() != null && !filter.name().isEmpty()) {
                appendWhere(whereClause, " l.name = :name ");
                parameters.put("name", filter.name());
            }

            if (filter.x() != null) {
                appendWhere(whereClause, " l.x = :x ");
                parameters.put("x", filter.x());
            }

            if (filter.y() != null) {
                appendWhere(whereClause, " l.y = :y ");
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
