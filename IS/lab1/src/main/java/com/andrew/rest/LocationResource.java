package com.andrew.rest;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import com.andrew.dto.OwnerResponse;
import com.andrew.dto.location.LocationRequest;
import com.andrew.dto.location.LocationResponse;
import com.andrew.model.Location;
import com.andrew.service.LocationService;

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
        @QueryParam("mine") @DefaultValue("false") boolean mine
    ) {
        List<Location> list = locationService.getAllLocations(mine);

        List<LocationResponse> responseList = list.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());

        return Response.ok(responseList).build();
    }

    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") int id) {
        Location location = locationService.getLocationById(id);
        return Response.ok(toResponse(location)).build();
    }

    @POST
    public Response create(@Valid LocationRequest request, @Context UriInfo uriInfo) {
        Location created = locationService.createLocation(request);

        URI location = uriInfo.getAbsolutePathBuilder()
                              .path(String.valueOf(created.getId()))
                              .build();
        
        return Response.created(location)
                       .entity(toResponse(created))
                       .build();
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") int id, @Valid LocationRequest request) {
        Location updated = locationService.updateLocation(id, request);
        return Response.ok(toResponse(updated)).build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") int id) {
        locationService.deleteLocation(id);
        return Response.noContent().build();
    }

    private LocationResponse toResponse(Location entity) {
        OwnerResponse owner = new OwnerResponse(
            entity.getOwner().getId(),
            entity.getOwner().getUsername()
        );

        return new LocationResponse(
            entity.getId(),
            owner,
            entity.getX(),
            entity.getY(),
            entity.getName()
        );
    }
}
