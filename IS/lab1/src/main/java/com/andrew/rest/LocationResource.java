package com.andrew.rest;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.andrew.dto.PageResponse;
import com.andrew.dto.location.LocationFilter;
import com.andrew.dto.location.LocationRequest;
import com.andrew.dto.location.LocationResponse;
import com.andrew.model.Location;
import com.andrew.service.LocationService;
import com.andrew.util.ResponseMapper;
import com.andrew.websocket.LocationSocketServer;
import com.andrew.websocket.WebSocketMessage;

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

@Path("/location")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LocationResource {
    @Inject
    LocationService locationService;

    @GET
    public Response getAll(
        @QueryParam("mine") @DefaultValue("false") boolean mine,
        
        @QueryParam("page") @DefaultValue("0") int page,
        @QueryParam("size") @DefaultValue("10") int size,
        @QueryParam("sort") @DefaultValue("id") String sort,
        @QueryParam("order") @DefaultValue("asc") String order,

        @QueryParam("owner.id") Long ownerId,
        @QueryParam("id") Long id,
        @QueryParam("name") String name,
        @QueryParam("x") Double x,
        @QueryParam("y") Double y
    ) {
        LocationFilter filter = new LocationFilter(ownerId, id, name, x, y);

        PageResponse<Location> pagedResult = locationService.getAllLocations(mine, page, size, sort, order, filter);

        List<LocationResponse> responseList = pagedResult.content().stream()
            .map(ResponseMapper::toResponse)
            .collect(Collectors.toList());

        PageResponse<LocationResponse> response = new PageResponse<>(
            responseList,
            pagedResult.totalElements()
        );

        return Response.ok(response).build();
    }

    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") int id) {
        Location location = locationService.getLocationById(id);
        return Response.ok(ResponseMapper.toResponse(location)).build();
    }

    @POST
    public Response create(@Valid LocationRequest request, @Context UriInfo uriInfo) {
        Location created = locationService.createLocation(request);

        URI location = uriInfo.getAbsolutePathBuilder()
                              .path(String.valueOf(created.getId()))
                              .build();
        
        LocationResponse response = ResponseMapper.toResponse(created);

        LocationSocketServer.broadcast(new WebSocketMessage<>("create", response));

        return Response.created(location)
                       .entity(response)
                       .build();
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") int id, @Valid LocationRequest request) {
        Location updated = locationService.updateLocation(id, request);
        LocationResponse response = ResponseMapper.toResponse(updated);
        LocationSocketServer.broadcast(new WebSocketMessage<>("update", response));
        return Response.ok(response).build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") int id) {
        locationService.deleteLocation(id);
        LocationSocketServer.broadcast(new WebSocketMessage<>("delete", Map.of("id", id)));
        return Response.noContent().build();
    }
}
