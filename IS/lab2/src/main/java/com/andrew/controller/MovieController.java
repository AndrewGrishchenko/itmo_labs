package com.andrew.controller;

import java.net.URI;
import java.util.Map;

import com.andrew.dto.PageResponse;
import com.andrew.dto.movie.MovieFilter;
import com.andrew.dto.movie.MovieRequest;
import com.andrew.dto.movie.MovieResponse;
import com.andrew.model.MovieGenre;
import com.andrew.model.MpaaRating;
import com.andrew.service.MovieService;
import com.andrew.websocket.WebSocketMessage;
import com.andrew.websocket.WebSocketNotifier;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/movie")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MovieController {
    @Inject
    MovieService movieService;

    @Inject
    private WebSocketNotifier notifier;

    @GET
    public Response getAll(
        @QueryParam("mine") @DefaultValue("false") boolean mine,
        
        @QueryParam("page") @DefaultValue("0") int page,
        @QueryParam("size") @DefaultValue("10") int size,
        @QueryParam("sort") @DefaultValue("id") String sort,
        @QueryParam("order") @DefaultValue("asc") String order,

        @QueryParam("filterLogic") @DefaultValue("AND") String filterLogic,
        
        @QueryParam("owner.id") Long ownerId,
        @QueryParam("id") Long id,
        @QueryParam("name") String name,
        @QueryParam("coordinates") Long coordinatesId,
        @QueryParam("creationDate") String creationDate,
        @QueryParam("oscarsCount") Long oscarsCount,
        @QueryParam("budget") Float budget,
        @QueryParam("totalBoxOffice") Double totalBoxOffice,
        @QueryParam("mpaaRating") MpaaRating mpaaRating,
        @QueryParam("director") Long directorId,
        @QueryParam("screenwriter") Long screenwriterId,
        @QueryParam("operator") Long operatorId,
        @QueryParam("length") Long length,
        @QueryParam("goldenPalmCount") Integer goldenPalmCount,
        @QueryParam("genre") MovieGenre genre
    ) {
        MovieFilter filter = new MovieFilter(
            ownerId,
            id,
            name,
            coordinatesId,
            creationDate,
            oscarsCount,
            budget,
            totalBoxOffice,
            mpaaRating,
            directorId,
            screenwriterId,
            operatorId,
            length,
            goldenPalmCount,
            genre
        );

        PageResponse<MovieResponse> response = movieService.getAllMovies(mine, page, size, sort, order, filterLogic, filter);

        return Response.ok(response).build();
    }

    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") Long id) {
        MovieResponse movie = movieService.getMovieById(id);
        return Response.ok(movie).build();
    }

    @POST
    public Response create(@Valid MovieRequest request, @Context UriInfo uriInfo) {
        MovieResponse created = movieService.createMovie(request);

        URI location = uriInfo.getAbsolutePathBuilder()
                              .path(String.valueOf(created.id()))
                              .build();

        notifier.broadcast("movie", new WebSocketMessage<>("create", created));

        return Response.created(location)
                       .entity(created)
                       .build();
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") Long id, @Valid MovieRequest request) {
        MovieResponse updated = movieService.updateMovie(id, request);
        notifier.broadcast("movie", new WebSocketMessage<>("update", updated));
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") Long id) {
        movieService.deleteMovie(id);
        notifier.broadcast("movie", new WebSocketMessage<>("delete", Map.of("id", id)));
        return Response.noContent().build();
    }
}
