package com.andrew.service;

import java.util.List;
import java.util.stream.Collectors;

import com.andrew.dto.PageResponse;
import com.andrew.dto.movie.MovieFilter;
import com.andrew.dto.movie.MovieRaw;
import com.andrew.dto.movie.MovieRequest;
import com.andrew.dto.movie.MovieResponse;
import com.andrew.exceptions.NotFoundException;
import com.andrew.exceptions.ValidationException;
import com.andrew.model.Movie;
import com.andrew.repository.MovieRepository;
import com.andrew.security.CurrentUser;
import com.andrew.util.ObjectFactory;
import com.andrew.util.ResponseMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ForbiddenException;

@ApplicationScoped
public class MovieService {
    @Inject
    MovieRepository movieRepository;

    @Inject
    CurrentUser currentUser;

    @Inject
    EntityResolver resolver;

    @Transactional
    public MovieResponse createMovie(MovieRequest dto) {
        return createMovie(ObjectFactory.createMovie(dto, currentUser.getUser()));
    }

    @Transactional
    public MovieResponse createMovie(MovieRaw dto) {
        return createMovie(ObjectFactory.createMovie(dto, currentUser.getUser()));
    }

    @Transactional
    private MovieResponse createMovie(Movie movie) {
        movie.setCoordinates(resolver.resolve(movie.getCoordinates()));
        movie.setDirector(resolver.resolve(movie.getDirector()));
        movie.setScreenwriter(resolver.resolve(movie.getScreenwriter()));
        movie.setOperator(resolver.resolve(movie.getOperator()));
        
        if (movieRepository.existsByNameAndDirector(movie.getName(), movie.getDirector())) {
            throw new ValidationException(
                String.format("copyright infringement: director %s has already released movie \"%s\"",
                    movie.getDirector().getName(), movie.getName())
            );
        }

        movieRepository.save(movie);
        return ResponseMapper.toResponse(movie);
    }

    @Transactional
    public MovieResponse getMovieById(Long id) {
        return ResponseMapper.toResponse(movieRepository.findById(id)
                       .orElseThrow(() -> new NotFoundException("Movie with id " + id + " not found")));
    }

    @Transactional
    public PageResponse<MovieResponse> getAllMovies(boolean mine, int page, int size, String sort, String order, String filterLogic, MovieFilter filter) {
        PageResponse<Movie> pagedResponse = mine ?
            movieRepository.findAllByUserPaginatedAndSorted(currentUser.getUser(), page, size, sort, order, filterLogic, filter) :
            movieRepository.findAllPaginatedAndSorted(page, size, sort, order, filterLogic, filter);

        return new PageResponse<MovieResponse>(
            pagedResponse.content().stream()
                .map(ResponseMapper::toResponse)
                .collect(Collectors.toList()),
            pagedResponse.totalElements()
        );
    }

    @Transactional
    public List<MovieResponse> getMoviesByGenres(List<String> genres) {
        return movieRepository.findByGenres(genres).stream()
            .map(ResponseMapper::toResponse)
            .toList();
    }

    @Transactional
    public MovieResponse updateMovie(Long id, MovieRequest dto) {
        Movie existing = movieRepository.findById(id)
                                 .orElseThrow(() -> new NotFoundException("Movie with id " + id + " not found"));
        
        if (!currentUser.isAdmin() && !existing.getOwner().getId().equals(currentUser.getUser().getId()))
            throw new ForbiddenException("No permission to update");
        
        existing.setName(dto.name());
        existing.setCoordinates(resolver.resolve(dto.coordinates()));
        existing.setOscarsCount(dto.oscarsCount());
        existing.setBudget(dto.budget());
        existing.setTotalBoxOffice(dto.totalBoxOffice());
        existing.setMpaaRating(dto.mpaaRating());
        existing.setDirector(resolver.resolve(dto.director()));
        existing.setScreenwriter(resolver.resolve(dto.screenwriter()));
        existing.setOperator(resolver.resolve(dto.operator()));
        existing.setLength(dto.length());
        existing.setGoldenPalmCount(dto.goldenPalmCount());
        existing.setGenre(dto.genre());

        return ResponseMapper.toResponse(movieRepository.update(existing));
    }

    @Transactional
    public void deleteMovie(Long id) {
        Movie movie = movieRepository.findById(id)
                              .orElseThrow(() -> new NotFoundException("Movie with id " + id + " not found"));
        
        if (!currentUser.isAdmin() && !movie.getOwner().getId().equals(currentUser.getUser().getId()))
            throw new ForbiddenException("No permission to delete");
        
        movieRepository.delete(movie);
    }
}