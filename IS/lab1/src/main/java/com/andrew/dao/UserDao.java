package com.andrew.dao;

import com.andrew.dto.PageResponse;
import com.andrew.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
public class UserDao {
    @Inject
    Session session;

    public void save(User user) {
        session.beginTransaction();
        session.persist(user);
        session.getTransaction().commit();
    }

    public PageResponse<User> findAllPaginatedAndSorted(int page, int size, String sortField, String sortOrder) {
        String countHql = "select count(u.id) from User u";
        Query<Long> countQuery = session.createQuery(countHql, Long.class);
        long totalElements = countQuery.getSingleResult();

        if (totalElements == 0)
            return new PageResponse<>(List.of(), 0);

        String sanitizedSortField = sanitizeSortField(sortField);
        String sanitizedSortOrder = "desc".equalsIgnoreCase(sortOrder) ? "desc" : "asc";

        StringBuilder hql = new StringBuilder("from User u order by u.");
        hql.append(sanitizedSortField);
        hql.append(" ");
        hql.append(sanitizedSortOrder);

        Query<User> query = session.createQuery(hql.toString(), User.class);
        query.setFirstResult(page * size);
        query.setMaxResults(size);

        List<User> content = query.list();

        return new PageResponse<>(content, totalElements);
    }

    public Optional<User> findById(Long id) {
        return session.createQuery("from User u where u.id = :id", User.class)
                      .setParameter("id", id)
                      .uniqueResultOptional();
    }

    public Optional<User> findByUsername(String username) {
        return session.createQuery("from User u where u.username = :username", User.class)
                      .setParameter("username", username)
                      .uniqueResultOptional();
    }

    public void delete(User user) {
        session.beginTransaction();
        session.remove(user);
        session.getTransaction().commit();
    }

    private String sanitizeSortField(String sortField) {
        Set<String> allowedSortFields = Set.of("id", "username");

        if (sortField != null && allowedSortFields.contains(sortField))
            return sortField;

        return "id";
    }
}
