package com.andrew.rest;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import com.andrew.dto.OwnerResponse;
import com.andrew.dto.coordinates.CoordinatesResponse;
import com.andrew.dto.coordinates.CoordinatesRequest;
import com.andrew.model.Coordinates;
import com.andrew.service.CoordinatesService;

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
    CoordinatesService coordinatesService;

    @GET
    public Response getAll(
        @QueryParam("mine") @DefaultValue("false") boolean mine
    ) {
        List<Coordinates> list = coordinatesService.getAllCoordinates(mine);
        
        List<CoordinatesResponse> responseList = list.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
        
        return Response.ok(responseList).build();
    }

    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") int id) {
        Coordinates coordinates = coordinatesService.getCoordinatesById(id);
        return Response.ok(toResponse(coordinates)).build();
    }

    @POST
    public Response create(@Valid CoordinatesRequest request, @Context UriInfo uriInfo) {
        Coordinates created = coordinatesService.createCoordinates(request);
        
        URI location = uriInfo.getAbsolutePathBuilder()
                              .path(String.valueOf(created.getId()))
                              .build();

        return Response.created(location)
                       .entity(toResponse(created))
                       .build();
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") int id, @Valid CoordinatesRequest dto) {
        Coordinates updated = coordinatesService.updateCoordinates(id, dto);
        return Response.ok(toResponse(updated)).build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") int id) {
        coordinatesService.deleteCoordinates(id);
        return Response.noContent().build();
    }

    private CoordinatesResponse toResponse(Coordinates entity) {
        OwnerResponse owner = new OwnerResponse(
            entity.getOwner().getId(),
            entity.getOwner().getUsername()
        );

        return new CoordinatesResponse(
            entity.getId(),
            owner,
            entity.getX(),
            entity.getY()
        );
    }
}
