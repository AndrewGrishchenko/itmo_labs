package com.andrew.service;

import java.util.List;

import com.andrew.dao.MovieDao;
import com.andrew.dto.movie.MovieRequest;
import com.andrew.exceptions.NotFoundException;
import com.andrew.model.Movie;
import com.andrew.security.CurrentUser;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ForbiddenException;

@ApplicationScoped
public class MovieService {
    @Inject
    MovieDao movieDao;

    @Inject
    CurrentUser currentUser;

    public Movie createMovie(MovieRequest dto) {
        Movie movie = new Movie(
            dto.name(),
            dto.coordinates(),
            dto.oscarsCount(),
            dto.budget(),
            dto.totalBoxOffice(),
            dto.mpaaRating(),
            dto.director(),
            dto.screenwriter(),
            dto.operator(),
            dto.length(),
            dto.goldenPalmCount(),
            dto.genre()
        );
        movie.setOwner(currentUser.getUser());

        movieDao.save(movie);
        return movie;
    }

    public Movie getMovieById(int id) {
        return movieDao.findById(id)
                       .orElseThrow(() -> new NotFoundException("Movie with id " + id + " not found"));
    }

    public List<Movie> getAllMovies(boolean mine) {
        return mine ? movieDao.getAllUser(currentUser.getUser()) : movieDao.getAll();
    }

    public Movie updateMovie(int id, MovieRequest dto) {
        Movie existing = movieDao.findById(id)
                                 .orElseThrow(() -> new NotFoundException("Movie with id " + id + " not found"));
        
        if (!currentUser.isAdmin() && !existing.getOwner().getId().equals(currentUser.getUser().getId()))
            throw new ForbiddenException("No permission to update");
        
        existing.setName(dto.name());
        existing.setCoordinates(dto.coordinates());
        existing.setOscarsCount(dto.oscarsCount());
        existing.setBudget(dto.budget());
        existing.setTotalBoxOffice(dto.totalBoxOffice());
        existing.setMpaaRating(dto.mpaaRating());
        existing.setDirector(dto.director());
        existing.setScreenwriter(dto.screenwriter());
        existing.setOperator(dto.operator());
        existing.setLength(dto.length());
        existing.setGoldenPalmCount(dto.goldenPalmCount());
        existing.setGenre(dto.genre());

        return movieDao.update(existing);
    }

    public void deleteMovie(int id) {
        Movie movie = movieDao.findById(id)
                              .orElseThrow(() -> new NotFoundException("Movie with id " + id + " not found"));
        
        if (!currentUser.isAdmin() && !movie.getOwner().getId().equals(currentUser.getUser().getId()))
            throw new ForbiddenException("No permission to delete");
        
        movieDao.delete(movie);
    }
}
