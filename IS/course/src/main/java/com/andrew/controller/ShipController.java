package com.andrew.controller;

import java.net.URI;

import com.andrew.annotation.RequireRole;
import com.andrew.dto.ship.ShipCreateDTO;
import com.andrew.dto.ship.ShipResponseDTO;
import com.andrew.model.Role;
import com.andrew.service.ShipService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/ship")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ShipController {
    @Inject
    ShipService shipService;

    @GET
    @RequireRole({Role.CAPTAIN, Role.KEEPER})
    public Response getShips() {
        return Response.ok(shipService.getAll()).build();
    }

    @POST
    @RequireRole(Role.ADMIN)
    public Response createShip(@Valid ShipCreateDTO dto, @Context UriInfo uriInfo) {
        ShipResponseDTO created = shipService.createShip(dto);

        URI location = uriInfo.getAbsolutePathBuilder()
            .path(String.valueOf(created.id()))
            .build();

        return Response.created(location)
            .entity(created)
            .build();
    }

    @PUT
    @Path("/{id}")
    @RequireRole(Role.ADMIN)
    public Response updateShip(@PathParam("id") Long id, @Valid ShipCreateDTO dto) {
        return Response.ok(shipService.updateShip(id, dto)).build();
    }

    @DELETE
    @Path("/{id}")
    @RequireRole(Role.ADMIN)
    public Response deleteShip(@PathParam("id") Long id) {
        shipService.deleteShip(id);
        return Response.ok().build();
    }
}
