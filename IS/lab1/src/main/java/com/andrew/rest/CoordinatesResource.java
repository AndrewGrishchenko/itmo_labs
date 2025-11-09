package com.andrew.rest;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.andrew.dto.PageResponse;
import com.andrew.dto.coordinates.CoordinatesResponse;
import com.andrew.dto.coordinates.CoordinatesFilter;
import com.andrew.dto.coordinates.CoordinatesRequest;
import com.andrew.model.Coordinates;
import com.andrew.service.CoordinatesService;
import com.andrew.util.ResponseMapper;
// import com.andrew.websocket.CoordinatesSocketServer;
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

@Path("/coordinates")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CoordinatesResource {
    @Inject
    private CoordinatesService coordinatesService;

    @Inject
    private WebSocketNotifier notifier;

    @GET
    public Response getAll(
        @QueryParam("mine") @DefaultValue("false") boolean mine,
        
        @QueryParam("page") @DefaultValue("0") int page,
        @QueryParam("size") @DefaultValue("10") int size,
        @QueryParam("sort") @DefaultValue("id") String sort,
        @QueryParam("order") @DefaultValue("asc") String order,

        @QueryParam("owner.id") Long ownerId,
        @QueryParam("id") Long id,
        @QueryParam("x") Long x,
        @QueryParam("y") Double y
    ) {
        CoordinatesFilter filter = new CoordinatesFilter(ownerId, id, x, y);

        PageResponse<Coordinates> pagedResult = coordinatesService.getAllCoordinates(mine, page, size, sort, order, filter);

        List<CoordinatesResponse> responseList = pagedResult.content().stream()
            .map(ResponseMapper::toResponse)
            .collect(Collectors.toList());

        PageResponse<CoordinatesResponse> response = new PageResponse<>(
            responseList,
            pagedResult.totalElements()
        );

        return Response.ok(response).build();
    }

    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") int id) {
        Coordinates coordinates = coordinatesService.getCoordinatesById(id);
        return Response.ok(ResponseMapper.toResponse(coordinates)).build();
    }

    @POST
    public Response create(@Valid CoordinatesRequest request, @Context UriInfo uriInfo) {
        Coordinates created = coordinatesService.createCoordinates(request);
        
        URI location = uriInfo.getAbsolutePathBuilder()
                              .path(String.valueOf(created.getId()))
                              .build();

        CoordinatesResponse response = ResponseMapper.toResponse(created);

        // CoordinatesSocketServer.broadcast(new WebSocketMessage<>("create", response));

        notifier.broadcast("coordinates", new WebSocketMessage<>("create", response));

        return Response.created(location)
                       .entity(response)
                       .build();
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") int id, @Valid CoordinatesRequest dto) {
        Coordinates updated = coordinatesService.updateCoordinates(id, dto);
        CoordinatesResponse response = ResponseMapper.toResponse(updated);
        // CoordinatesSocketServer.broadcast(new WebSocketMessage<>("update", response));
        notifier.broadcast("coordinates", new WebSocketMessage<>("update", response));
        return Response.ok(response).build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") int id) {
        coordinatesService.deleteCoordinates(id);
        // CoordinatesSocketServer.broadcast(new WebSocketMessage<>("delete", Map.of("id", id)));
        notifier.broadcast("coordinates", new WebSocketMessage<>("delete", Map.of("id", id)));
        return Response.noContent().build();
    }
}
