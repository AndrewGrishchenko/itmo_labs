package com.andrew.repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.hibernate.SessionFactory;
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
public class MovieRepository {
    @Inject
    SessionFactory sessionFactory;

    public void save(Movie movie) {
        sessionFactory.getCurrentSession().persist(movie);
    }

    public List<Movie> getAll() {
        return sessionFactory.getCurrentSession().createQuery("from Movie", Movie.class).list();
    }

    public List<Movie> getAllUser(User user) {
        return sessionFactory.getCurrentSession().createQuery("from Movie m where m.owner = :user", Movie.class)
                      .setParameter("user", user)
                      .list();
    }

    public PageResponse<Movie> findAllPaginatedAndSorted(int page, int size, String sortField, String sortOrder, String filterLogic, MovieFilter filter) {
        return findInternal(null, page, size, sortField, sortOrder, filterLogic, filter);
    }

    public PageResponse<Movie> findAllByUserPaginatedAndSorted(User user, int page, int size, String sortField, String sortOrder, String filterLogic, MovieFilter filter) {
        return findInternal(user, page, size, sortField, sortOrder, filterLogic, filter);
    }

    private PageResponse<Movie> findInternal(User user, int page, int size, String sortField, String sortOrder, String filterLogic, MovieFilter filter) {
        Map<String, Object> parameters = new HashMap<>();
        String sanitizedFilterLogic = "and".equalsIgnoreCase(filterLogic) ? "and" : "or";
        String whereClause = buildWhereClause(filter, parameters, user, sanitizedFilterLogic);
        
        String countHql = "select count(m.id) from Movie m" + whereClause;
        Query<Long> countQuery = sessionFactory.getCurrentSession().createQuery(countHql, Long.class);
        setFilterParameters(countQuery, parameters);
        long totalElements = countQuery.getSingleResult();

        if (totalElements == 0)
            return new PageResponse<>(List.of(), 0);

        String sanitizedSortField = sanitizeSortField(sortField);
        String sanitizedSortOrder = "desc".equalsIgnoreCase(sanitizedSortField) ? "desc" : "asc";
        String hql = String.format("from Movie m %s order by m.%s %s", whereClause, sanitizedSortField, sanitizedSortOrder);

        Query<Movie> query = sessionFactory.getCurrentSession().createQuery(hql, Movie.class);
        setFilterParameters(query, parameters);
        query.setFirstResult(page * size);
        query.setMaxResults(size);

        List<Movie> content = query.list();
        return new PageResponse<>(content, totalElements);
    }

    public Optional<Movie> findById(Long id) {
        return sessionFactory.getCurrentSession().createQuery("from Movie m where m.id = :id", Movie.class)
                      .setParameter("id", id)
                      .uniqueResultOptional();
    }

    public boolean existsByPersonAndNotOwner(Person person, User owner) {
        return sessionFactory.getCurrentSession().createQuery("select count(m.id) from Movie m where " +
                                   "m.owner != :owner and (" +
                                   "m.director = :person or " +
                                   "m.screenwriter = :person or " +
                                   "m.operator = :person)", Long.class)
                      .setParameter("owner", owner)
                      .setParameter("person", person)
                      .getSingleResult() > 0;
    }

    public List<Movie> findByPerson(Person person) {
        return sessionFactory.getCurrentSession().createQuery("from Movie m where " +
                                   "m.director = :person or " +
                                   "m.screenwriter = :person or " +
                                   "m.operator = :person", Movie.class)
                      .setParameter("person", person)
                      .list();
    }

    public List<Movie> findByGenres(List<String> genres) {
        return sessionFactory.getCurrentSession().createQuery("from Movie m WHERE m.genre IN (:genres)", Movie.class)
                      .setParameter("genres", genres)
                      .getResultList();
    }

    public boolean existsByCoordinateAndNotOwner(Coordinates coordinates, User owner) {
        return sessionFactory.getCurrentSession().createQuery("select count(m.id) from Movie m where " +
                                   "m.owner != :owner and " +
                                   "m.coordinates = :coordinates", Long.class)
                      .setParameter("owner", owner)
                      .setParameter("coordinates", coordinates)
                      .getSingleResult() > 0;
    }

    public List<Movie> findByCoordinate(Coordinates coordinates) {
        return sessionFactory.getCurrentSession().createQuery("from Movie m where m.coordinates = :coordinates", Movie.class)
                      .setParameter("coordinates", coordinates)
                      .list();
    }

    public boolean existsByNameAndDirector(String name, Person director) {
        return sessionFactory.getCurrentSession().createQuery("select count(m.id) from Movie m where " +
                                    "m.name = :name and " +
                                    "m.director = :director", Long.class)
                        .setParameter("name", name)
                        .setParameter("director", director)
                        .getSingleResult() > 0;
    }

    public Movie update(Movie movie) {
        Movie updated = sessionFactory.getCurrentSession().merge(movie);
        return updated;
    }

    public void delete(Movie movie) {
        sessionFactory.getCurrentSession().remove(movie);
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
