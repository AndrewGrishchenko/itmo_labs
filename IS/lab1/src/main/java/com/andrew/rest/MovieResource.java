package com.andrew.rest;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import com.andrew.dto.OwnerResponse;
import com.andrew.dto.movie.MovieRequest;
import com.andrew.dto.movie.MovieResponse;
import com.andrew.model.Movie;
import com.andrew.service.MovieService;

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

    @GET
    public Response getAll(
        @QueryParam("mine") @DefaultValue("false") boolean mine
    ) {
        List<Movie> list = movieService.getAllMovies(mine);

        List<MovieResponse> responseList = list.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
        
        return Response.ok(responseList).build();
    }

    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") int id) {
        Movie movie = movieService.getMovieById(id);
        return Response.ok(toResponse(movie)).build();
    }

    @POST
    public Response create(@Valid MovieRequest request, @Context UriInfo uriInfo) {
        Movie created = movieService.createMovie(request);

        URI location = uriInfo.getAbsolutePathBuilder()
                              .path(String.valueOf(created.getId()))
                              .build();

        return Response.created(location)
                       .entity(toResponse(created))
                       .build();
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") int id, @Valid MovieRequest request) {
        Movie updated = movieService.updateMovie(id, request);
        return Response.ok(toResponse(updated)).build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") int id) {
        movieService.deleteMovie(id);
        return Response.noContent().build();
    }

    private MovieResponse toResponse(Movie entity) {
        OwnerResponse owner = new OwnerResponse(
            entity.getOwner().getId(),
            entity.getOwner().getUsername()
        );

        return new MovieResponse(
            entity.getId(),
            owner,
            entity.getName(),
            entity.getCoordinates(),
            entity.getOscarsCount(),
            entity.getBudget(),
            entity.getTotalBoxOffice(),
            entity.getMpaaRating(),
            entity.getDirector(),
            entity.getScreenwriter(),
            entity.getOperator(),
            entity.getLength(),
            entity.getGoldenPalmCount(),
            entity.getGenre()
        );
    }
}
