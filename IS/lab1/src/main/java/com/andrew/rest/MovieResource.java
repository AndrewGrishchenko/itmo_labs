package com.andrew.rest;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.andrew.dto.PageResponse;
import com.andrew.dto.movie.MovieFilter;
import com.andrew.dto.movie.MovieRequest;
import com.andrew.dto.movie.MovieResponse;
import com.andrew.model.Movie;
import com.andrew.model.MovieGenre;
import com.andrew.model.MpaaRating;
import com.andrew.service.MovieService;
import com.andrew.util.ResponseMapper;
// import com.andrew.websocket.MovieSocketServer;
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
public class MovieResource {
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

        PageResponse<Movie> pagedResult = movieService.getAllMovies(mine, page, size, sort, order, filterLogic, filter);

        List<MovieResponse> responseList = pagedResult.content().stream()
            .map(ResponseMapper::toResponse)
            .collect(Collectors.toList());

        PageResponse<MovieResponse> response = new PageResponse<>(
            responseList,
            pagedResult.totalElements()
        );
                
        return Response.ok(response).build();
    }

    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") int id) {
        Movie movie = movieService.getMovieById(id);
        return Response.ok(ResponseMapper.toResponse(movie)).build();
    }

    @POST
    public Response create(@Valid MovieRequest request, @Context UriInfo uriInfo) {
        Movie created = movieService.createMovie(request);

        URI location = uriInfo.getAbsolutePathBuilder()
                              .path(String.valueOf(created.getId()))
                              .build();

        MovieResponse response = ResponseMapper.toResponse(created);

        // MovieSocketServer.broadcast(new WebSocketMessage<>("create", response));

        notifier.broadcast("movie", new WebSocketMessage<>("create", response));

        return Response.created(location)
                       .entity(response)
                       .build();
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") int id, @Valid MovieRequest request) {
        Movie updated = movieService.updateMovie(id, request);
        MovieResponse response = ResponseMapper.toResponse(updated);
        // MovieSocketServer.broadcast(new WebSocketMessage<>("update", response));
        notifier.broadcast("movie", new WebSocketMessage<>("update", response));
        return Response.ok(response).build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") int id) {
        movieService.deleteMovie(id);
        // MovieSocketServer.broadcast(new WebSocketMessage<>("delete", Map.of("id", id)));
        notifier.broadcast("movie", new WebSocketMessage<>("delete", Map.of("id", id)));
        return Response.noContent().build();
    }
}
