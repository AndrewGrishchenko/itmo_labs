package com.andrew.dao;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.andrew.dto.PageResponse;
import com.andrew.dto.movie.MovieFilter;
import com.andrew.model.Movie;
import com.andrew.model.User;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MovieDao {
    @Inject
    Session session;

    public void save(Movie movie) {
        session.beginTransaction();
        session.persist(movie);
        session.getTransaction().commit();
    }

    public List<Movie> getAll() {
        return session.createQuery("from Movie", Movie.class).list();
    }

    public List<Movie> getAllUser(User user) {
        return session.createQuery("from Movie m where m.owner = :user", Movie.class)
                      .setParameter("user", user)
                      .list();
    }

    public PageResponse<Movie> findAllPaginatedAndSorted(int page, int size, String sortField, String sortOrder, MovieFilter filter) {
        Map<String, Object> parameters = new HashMap<>();
        String whereClause = buildWhereClause(filter, parameters, null);

        String countHql = "select count(m.id) from Movie m" + whereClause;
        Query<Long> countQuery = session.createQuery(countHql, Long.class);
        setFilterParameters(countQuery, parameters);
        long totalElements = countQuery.getSingleResult();

        if (totalElements == 0)
            return new PageResponse<>(List.of(), 0);

        String sanitizedSortField = sanitizeSortField(sortField);
        String sanitizedSortOrder = "desc".equalsIgnoreCase(sortOrder) ? "desc" : "asc";
        String hql = String.format("from Movie m %s order by m.%s %s", whereClause, sanitizedSortField, sanitizedSortOrder);
        
        Query<Movie> query = session.createQuery(hql, Movie.class);
        setFilterParameters(query, parameters);
        query.setFirstResult(page * size);
        query.setMaxResults(size);

        List<Movie> content = query.list();
        return new PageResponse<>(content, totalElements);
    }

    public PageResponse<Movie> findAllByUserPaginatedAndSorted(User user, int page, int size, String sortField, String sortOrder, MovieFilter filter) {
        Map<String, Object> parameters = new HashMap<>();
        String whereClause = buildWhereClause(filter, parameters, user);

        String countHql = "select count(m.id) from Movie m" + whereClause;
        Query<Long> countQuery = session.createQuery(countHql, Long.class);
        setFilterParameters(countQuery, parameters);
        long totalElements = countQuery.getSingleResult();

        if (totalElements == 0)
            return new PageResponse<>(List.of(), 0);

        String sanitizedSortField = sanitizeSortField(sortField);
        String sanitizedSortOrder = "desc".equalsIgnoreCase(sortOrder) ? "desc" : "asc";
        String hql = String.format("from Movie m %s order by m.%s %s", whereClause, sanitizedSortField, sanitizedSortOrder);
        
        Query<Movie> query = session.createQuery(hql, Movie.class);
        setFilterParameters(query, parameters);
        query.setFirstResult(page * size);
        query.setMaxResults(size);

        List<Movie> content = query.list();
        return new PageResponse<>(content, totalElements);
    }

    public Optional<Movie> findById(int id) {
        return session.createQuery("from Movie m where m.id = :id", Movie.class)
                      .setParameter("id", id)
                      .uniqueResultOptional();
    }

    public Movie update(Movie movie) {
        session.beginTransaction();
        Movie updated = session.merge(movie);
        session.getTransaction().commit();
        return updated;
    }

    public void delete(Movie movie) {
        session.beginTransaction();
        session.remove(movie);
        session.getTransaction().commit();
    }

    private String sanitizeSortField(String sortField) {
        Set<String> allowedSortFields = Set.of("id", "name", "coordinates", "creationDate", "oscarsCount", "budget", "totalBoxOffice", "mpaaRating", "director", "screenwriter", "operator", "length", "goldenPalmCount", "genre", "owner.username");

        if (sortField != null && allowedSortFields.contains(sortField))
            return sortField;

        return "id";
    }

    private String buildWhereClause (MovieFilter filter, Map<String, Object> parameters, User user) {
        StringBuilder whereClause = new StringBuilder();

        if (user != null) {
            appendWhere(whereClause, " m.owner = :user ");
            parameters.put("user", user);
        }

        if (filter != null) {
            if (filter.ownerId() != null) {
                appendWhere(whereClause, " m.owner.id = :ownerId ");
                parameters.put("ownerId", filter.ownerId());
            }

            if (filter.name() != null && !filter.name().isEmpty()) {
                appendWhere(whereClause, " m.name = :name ");
                parameters.put("name", filter.name());
            }

            if (filter.coordinatesId() != null) {
                appendWhere(whereClause, " m.coordinates.id = :coordinatesId ");
                parameters.put("coordinatesId", filter.coordinatesId());
            }

            if (filter.creationDate() != null && !filter.creationDate().isEmpty()) {
                appendWhere(whereClause, " m.creationDate = :creationDate ");
                parameters.put("creationDate", LocalDateTime.parse(filter.creationDate(), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
            }

            if (filter.oscarsCount() != null) {
                appendWhere(whereClause, " m.oscarsCount = :oscarsCount ");
                parameters.put("oscarsCount", filter.oscarsCount());
            }

            if (filter.budget() != null) {
                appendWhere(whereClause, " m.budget = :budget ");
                parameters.put("budget", filter.budget());
            }

            if (filter.totalBoxOffice() != null) {
                appendWhere(whereClause, " m.totalBoxOffice = :totalBoxOffice ");
                parameters.put("totalBoxOffice", filter.totalBoxOffice());
            }

            if (filter.mpaaRating() != null) {
                appendWhere(whereClause, " m.mpaaRating = :mpaaRating ");
                parameters.put("mpaaRating", filter.mpaaRating());
            }

            if (filter.directorId() != null) {
                appendWhere(whereClause, " m.director.id = :directorId ");
                parameters.put("directorId", filter.directorId());
            }

            if (filter.screenwriterId() != null) {
                appendWhere(whereClause, " m.screenwriter.id = :screenwriterId ");
                parameters.put("screenwriterId", filter.screenwriterId());
            }

            if (filter.operatorId() != null) {
                appendWhere(whereClause, " m.operator.id = :operatorId ");
                parameters.put("operatorId", filter.operatorId());
            }

            if (filter.length() != null) {
                appendWhere(whereClause, " m.length = :length ");
                parameters.put("length", filter.length());
            }

            if (filter.goldenPalmCount() != null) {
                appendWhere(whereClause, " m.goldenPalmCount = :goldenPalmCount ");
                parameters.put("goldenPalmCount", filter.goldenPalmCount());
            }

            if (filter.genre() != null) {
                appendWhere(whereClause, " m.genre = :genre ");
                parameters.put("genre", filter.genre());
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
