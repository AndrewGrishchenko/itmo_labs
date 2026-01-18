package com.andrew.controller;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.andrew.dto.RedistributeOscarRequest;
import com.andrew.dto.import_history.ImportResult;
import com.andrew.dto.movie.MovieResponse;
import com.andrew.dto.person.PersonResponse;
import com.andrew.service.MovieService;
import com.andrew.service.OperationService;
import com.andrew.websocket.WebSocketMessage;
import com.andrew.websocket.WebSocketNotifier;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/operation")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OperationController {
    @Inject
    OperationService operationService;

    @Inject
    MovieService movieService;

    @Inject
    WebSocketNotifier notifier;

    @GET
    @Path("/movie-min-genre")
    public Response getMovieWithMinGenre() {
        MovieResponse movie = operationService.findMovieWithMinGenre();
        
        if (movie == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        
        return Response.ok(movie).build();
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
        List<PersonResponse> result = operationService.findScreenwritersWithNoOscars();
        
        return Response.ok(result).build();
    }

    @POST
    @Path("/redistribute-oscars")
    public Response redistributeOscars(@Valid RedistributeOscarRequest dto) {
        Long movedOscars = operationService.redistributeOscars(dto.sourceGenre(), dto.destGenre());
    
        if (movedOscars != null && movedOscars > 0) {
            List<String> affectedGenres = List.of(dto.sourceGenre(), dto.destGenre());
            List<MovieResponse> updatedMovies = movieService.getMoviesByGenres(affectedGenres);
            
            updatedMovies.forEach(movie -> {
                notifier.broadcast("movie", new WebSocketMessage<>("update", movie));
            });
        }
        
        return Response.ok(Map.of("movedOscars", movedOscars)).build();
    }

    @POST
    @Path("/import")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response importData(@FormParam("file") EntityPart filePart) {
        if (filePart == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", "file is missing"))
                .build();
        }

        InputStream stream = filePart.getContent();
        ImportResult result = operationService.parseObjects(stream);

        result.getCoordinates().forEach((coord) -> {
            notifier.broadcast("coordinates", new WebSocketMessage<>("create", coord));
        });

        result.getLocations().forEach((loc) -> {
            notifier.broadcast("location", new WebSocketMessage<>("create", loc));
        });

        result.getPersons().forEach((per) -> {
            notifier.broadcast("person", new WebSocketMessage<>("create", per));
            notifier.broadcast("location", new WebSocketMessage<>("create", per.location()));
        });

        result.getMovies().forEach((mov) -> {
            notifier.broadcast("movie", new WebSocketMessage<>("create", mov));
            if (mov.coordinates() != null) notifier.broadcast("coordinates", new WebSocketMessage<>("create", mov.coordinates()));
            broadcastPersonAndLocation(mov.director());
            broadcastPersonAndLocation(mov.screenwriter());
            broadcastPersonAndLocation(mov.operator());
        });

        return Response.ok(Map.of(
            "message", "import successful",
            "count", result.getTotalCount()
        )).build();
    }

    private void broadcastPersonAndLocation(PersonResponse person) {
        if (person == null) return;

        notifier.broadcast("person", new WebSocketMessage<>("create", person));

        if (person.location() != null) {
            notifier.broadcast("location", new WebSocketMessage<>("create", person.location()));
        }
    }
}
