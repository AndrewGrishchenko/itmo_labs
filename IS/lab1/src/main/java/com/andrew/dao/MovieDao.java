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
import com.andrew.model.Coordinates;
import com.andrew.model.Movie;
import com.andrew.model.Person;
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

    public PageResponse<Movie> findAllPaginatedAndSorted(int page, int size, String sortField, String sortOrder, String filterLogic, MovieFilter filter) {
        Map<String, Object> parameters = new HashMap<>();
        String sanitizedFilterLogic = "and".equalsIgnoreCase(filterLogic) ? "and" : "or";
        String whereClause = buildWhereClause(filter, parameters, null, sanitizedFilterLogic);

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

    public PageResponse<Movie> findAllByUserPaginatedAndSorted(User user, int page, int size, String sortField, String sortOrder, String filterLogic, MovieFilter filter) {
        Map<String, Object> parameters = new HashMap<>();
        String sanitizedFilterLogic = "and".equalsIgnoreCase(filterLogic) ? "and" : "or";
        String whereClause = buildWhereClause(filter, parameters, user, sanitizedFilterLogic);

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

    public boolean existsByPersonAndNotOwner(Person person, User owner) {
        return session.createQuery("select count(m.id) from Movie m where " +
                                   "m.owner != :owner and (" +
                                   "m.director = :person or " +
                                   "m.screenwriter = :person or " +
                                   "m.operator = :person)", Long.class)
                      .setParameter("owner", owner)
                      .setParameter("person", person)
                      .getSingleResult() > 0;
    }

    public List<Movie> findByPerson(Person person) {
        return session.createQuery("from Movie m where " +
                                   "m.director = :person or " +
                                   "m.screenwriter = :person or " +
                                   "m.operator = :person", Movie.class)
                      .setParameter("person", person)
                      .list();
    }

    public boolean existsByCoordinateAndNotOwner(Coordinates coordinates, User owner) {
        return session.createQuery("select count(m.id) from Movie m where " +
                                   "m.owner != :owner and " +
                                   "m.coordinates = :coordinates", Long.class)
                      .setParameter("owner", owner)
                      .setParameter("coordinates", coordinates)
                      .getSingleResult() > 0;
    }

    public List<Movie> findByCoordinate(Coordinates coordinates) {
        return session.createQuery("from Movie m where m.coordinates = :coordinates", Movie.class)
                      .setParameter("coordinates", coordinates)
                      .list();
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

    private String buildWhereClause (MovieFilter filter, Map<String, Object> parameters, User user, String filterLogic) {
        StringBuilder whereClause = new StringBuilder();

        if (user != null) {
            appendWhere(whereClause, " m.owner = :user ", filterLogic);
            parameters.put("user", user);
        }

        if (filter != null) {
            if (filter.ownerId() != null) {
                appendWhere(whereClause, " m.owner.id = :ownerId ", filterLogic);
                parameters.put("ownerId", filter.ownerId());
            }

            if (filter.id() != null) {
                appendWhere(whereClause, " m.id = :id ", filterLogic);
                parameters.put("id", filter.id());
            }

            if (filter.name() != null && !filter.name().isEmpty()) {
                appendWhere(whereClause, " m.name = :name ", filterLogic);
                parameters.put("name", filter.name());
            }

            if (filter.coordinatesId() != null) {
                appendWhere(whereClause, " m.coordinates.id = :coordinatesId ", filterLogic);
                parameters.put("coordinatesId", filter.coordinatesId());
            }

            if (filter.creationDate() != null && !filter.creationDate().isEmpty()) {
                appendWhere(whereClause, " m.creationDate = :creationDate ", filterLogic);
                parameters.put("creationDate", LocalDateTime.parse(filter.creationDate(), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
            }

            if (filter.oscarsCount() != null) {
                appendWhere(whereClause, " m.oscarsCount = :oscarsCount ", filterLogic);
                parameters.put("oscarsCount", filter.oscarsCount());
            }

            if (filter.budget() != null) {
                appendWhere(whereClause, " m.budget = :budget ", filterLogic);
                parameters.put("budget", filter.budget());
            }

            if (filter.totalBoxOffice() != null) {
                appendWhere(whereClause, " m.totalBoxOffice = :totalBoxOffice ", filterLogic);
                parameters.put("totalBoxOffice", filter.totalBoxOffice());
            }

            if (filter.mpaaRating() != null) {
                appendWhere(whereClause, " m.mpaaRating = :mpaaRating ", filterLogic);
                parameters.put("mpaaRating", filter.mpaaRating());
            }

            if (filter.directorId() != null) {
                appendWhere(whereClause, " m.director.id = :directorId ", filterLogic);
                parameters.put("directorId", filter.directorId());
            }

            if (filter.screenwriterId() != null) {
                appendWhere(whereClause, " m.screenwriter.id = :screenwriterId ", filterLogic);
                parameters.put("screenwriterId", filter.screenwriterId());
            }

            if (filter.operatorId() != null) {
                appendWhere(whereClause, " m.operator.id = :operatorId ", filterLogic);
                parameters.put("operatorId", filter.operatorId());
            }

            if (filter.length() != null) {
                appendWhere(whereClause, " m.length = :length ", filterLogic);
                parameters.put("length", filter.length());
            }

            if (filter.goldenPalmCount() != null) {
                appendWhere(whereClause, " m.goldenPalmCount = :goldenPalmCount ", filterLogic);
                parameters.put("goldenPalmCount", filter.goldenPalmCount());
            }

            if (filter.genre() != null) {
                appendWhere(whereClause, " m.genre = :genre ", filterLogic);
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

    private void appendWhere(StringBuilder sb, String condition, String filterLogic) {
        if (sb.length() == 0)
            sb.append(" where ");
        else
            sb.append(" " + filterLogic + " ");
        sb.append(condition);
    }
}
