package com.andrew.rest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.andrew.dto.RedistributeOscarRequest;
import com.andrew.dto.person.PersonResponse;
import com.andrew.model.Movie;
import com.andrew.service.MovieService;
import com.andrew.service.OperationService;
import com.andrew.util.ResponseMapper;
import com.andrew.websocket.WebSocketMessage;
import com.andrew.websocket.WebSocketNotifier;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/operation")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OperationResource {
    @Inject
    OperationService operationService;

    @Inject
    MovieService movieService;

    @Inject
    WebSocketNotifier notifier;

    @GET
    @Path("/movie-min-genre")
    public Response getMovieWithMinGenre() {
        Movie movie = operationService.findMovieWithMinGenre();
        
        if (movie == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        
        return Response.ok(ResponseMapper.toResponse(movie)).build();
    }

    @GET
    @Path("/count-by-golden-palm/{count}")
    public Response countByGoldenPalm(@PathParam("count") int count) {
        Long result = operationService.countMoviesByGoldenPalm(count);
        return Response.ok(Map.of("count", result)).build();
    }

    @GET
    @Path("/count-genre-less-than/{genre}")
    public Response countGenreLessThan(@PathParam("genre") String genre) {
        Long result = operationService.countMoviesGenreLessThan(genre);
        return Response.ok(Map.of("count", result)).build();
    }

    @GET
    @Path("/screenwriters-no-oscars")
    public Response getScreenwritersWithNoOscars() {
        List<PersonResponse> result = operationService.findScreenwritersWithNoOscars().stream()
            .map(ResponseMapper::toResponse)
            .collect(Collectors.toList());
        
        return Response.ok(result).build();
    }

    @POST
    @Path("/redistribute-oscars")
    public Response redistributeOscars(@Valid RedistributeOscarRequest dto) {
        Long movedOscars = operationService.redistributeOscars(dto.sourceGenre(), dto.destGenre());
    
        if (movedOscars != null && movedOscars > 0) {
            List<String> affectedGenres = List.of(dto.sourceGenre(), dto.destGenre());
            List<Movie> updatedMovies = movieService.getMoviesByGenres(affectedGenres);
            
            updatedMovies.forEach(movie -> {
                notifier.broadcast("movie", new WebSocketMessage<>("update", ResponseMapper.toResponse(movie)));
            });
        }
        
        return Response.ok(Map.of("movedOscars", movedOscars)).build();
    }
}
