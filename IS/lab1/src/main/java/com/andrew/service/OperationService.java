package com.andrew.service;

import java.util.List;

import com.andrew.dao.MovieDao;
import com.andrew.dao.OperationDao;
import com.andrew.model.Movie;
import com.andrew.model.Person;
import com.andrew.websocket.WebSocketNotifier;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class OperationService {
    @Inject
    OperationDao operationDao;

    @Inject
    MovieDao movieDao;

    @Inject
    WebSocketNotifier notifier;

    public Movie findMovieWithMinGenre() {
        return operationDao.findMovieWithMinGenre();
    }

    public Long countMoviesByGoldenPalm(int count) {
        return operationDao.countMoviesByGoldenPalm(count);
    }

    public Long countMoviesGenreLessThan(String genre) {
        return operationDao.countMoviesGenreLessThan(genre);
    }

    public List<Person> findScreenwritersWithNoOscars() {
        return operationDao.findScreenWritersWithNoOscars();
    }

    public Long redistributeOscars(String sourceGenre, String destGenre) {
        return operationDao.redistributeOscars(sourceGenre, destGenre);
    }
}
