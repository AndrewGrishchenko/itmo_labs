package com.andrew.dao;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;

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
}
