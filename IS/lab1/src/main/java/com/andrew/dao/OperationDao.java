package com.andrew.dao;

import java.util.Collections;
import java.util.List;

import org.hibernate.Session;

import com.andrew.model.Movie;
import com.andrew.model.Person;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class OperationDao {
    @Inject
    Session session;

    public Movie findMovieWithMinGenre() {
        Long movieId = session.createNativeQuery("SELECT find_movie_with_min_genre()", Long.class)
                              .getSingleResult();
        
        if (movieId == null)
            return null;

        return session.get(Movie.class, movieId);
    }

    public Long countMoviesByGoldenPalm(int count) {
        return session.createNativeQuery("SELECT count_movies_by_golden_palm(:palm_count)", Long.class)
                      .setParameter("palm_count", count)
                      .getSingleResult();
    }

    public Long countMoviesGenreLessThan(String genre) {
        return session.createNativeQuery("SELECT count_movies_genre_less_than(:genre_value)", Long.class)
                      .setParameter("genre_value", genre)
                      .getSingleResult();
    }

    public List<Person> findScreenWritersWithNoOscars() {
        List<Integer> personIds = session.createNativeQuery("SELECT find_screenwriters_with_no_oscars()", Integer.class).list();

        if (personIds == null || personIds.isEmpty())
            return Collections.emptyList();
        
        return session.createQuery("from Person p where p.id in (:ids)", Person.class)
                      .setParameter("ids", personIds)
                      .list();
    }

    public Long redistributeOscars(String sourceGenre, String destGenre) {
        return session.createNativeQuery("SELECT redistribute_oscars_by_genre(:source, :dest)", Long.class)
                      .setParameter("source", sourceGenre)
                      .setParameter("dest", destGenre)
                      .getSingleResult();
    }
}
