package com.andrew.dao;

import org.hibernate.Session;

import com.andrew.model.Movie;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RequestDao {
    @Inject
    Session session;

    public Movie getMovieWithMinGenre() {
        Long movieId = session.createQuery("SELECT get_movie_with_min_genre()", Long.class).getSingleResult();
        
        if (movieId == null)
            return null;

        return null;
    }
}
