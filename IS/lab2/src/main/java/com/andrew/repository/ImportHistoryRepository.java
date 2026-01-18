package com.andrew.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import com.andrew.dto.PageResponse;
import com.andrew.dto.import_history.ImportHistoryFilter;
import com.andrew.model.ImportHistory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ImportHistoryRepository {
    @Inject
    SessionFactory sessionFactory;

    public void save(ImportHistory importHistory) {
        sessionFactory.getCurrentSession().persist(importHistory);
    }

    public List<ImportHistory> getAll() {
        return sessionFactory.getCurrentSession().createQuery("from ImportHistory", ImportHistory.class).list();
    }

    public PageResponse<ImportHistory> findAllPaginatedAndSorted(int page, int size, String sortField, String sortOrder, ImportHistoryFilter filter) {
        Map<String, Object> parameters = new HashMap<>();
        String whereClause = buildWhereClause(filter, parameters);

        String countHql = "select count(ih.id) from ImportHistory ih" + whereClause;
        Query<Long> countQuery = sessionFactory.getCurrentSession().createQuery(countHql, Long.class);
        setFilterParameters(countQuery, parameters);
        long totalElements = countQuery.getSingleResult();

        if (totalElements == 0)
            return new PageResponse<>(List.of(), 0);

        String sanitizedSortField = sanitizeSortField(sortField);
        String sanitizedSortOrder = "desc".equalsIgnoreCase(sortOrder) ? "desc" : "asc";
        String hql = String.format("from ImportHistory ih %s order by ih.%s %s", whereClause, sanitizedSortField, sanitizedSortOrder);

        Query<ImportHistory> query = sessionFactory.getCurrentSession().createQuery(hql, ImportHistory.class);
        setFilterParameters(query, parameters);
        query.setFirstResult(page * size);
        query.setMaxResults(size);

        List<ImportHistory> content = query.list();
        return new PageResponse<>(content, totalElements);
    }

    public Optional<ImportHistory> findById(int id) {
        return sessionFactory.getCurrentSession().createQuery("from ImportHistory ih where ih.id = :id", ImportHistory.class)
            .setParameter("id", id)
            .uniqueResultOptional();
    }

    private String sanitizeSortField(String sortField) {
        Set<String> allowedSortFields = Set.of("id", "user", "operationStatus", "objectCount");

        if (sortField != null && allowedSortFields.contains(sortField))
            return sortField;

        return "id";
    }

    private String buildWhereClause(ImportHistoryFilter filter, Map<String, Object> parameters) {
        StringBuilder whereClause = new StringBuilder();

        if (filter != null) {
            if (filter.id() != null) {
                appendWhere(whereClause, " ih.id = :id ");
                parameters.put("id", filter.id());
            }

            if (filter.userId() != null) {
                appendWhere(whereClause, " ih.user.id = :userId ");
                parameters.put("userId", filter.userId());
            }

            if (filter.operationStatus() != null) {
                appendWhere(whereClause, " ih.operationStatus = :operationStatus ");
                parameters.put("operationStatus", filter.operationStatus());
            }

            if (filter.objectCount() != null) {
                appendWhere(whereClause, " ih.objectCount = :objectCount ");
                parameters.put("objectCount", filter.objectCount());
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
