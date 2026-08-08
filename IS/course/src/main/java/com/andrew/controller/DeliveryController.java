package com.andrew.controller;

import java.net.URI;

import com.andrew.annotation.RequireRole;
import com.andrew.dto.delivery.DeliveryCreateDTO;
import com.andrew.dto.delivery.DeliveryResponseDTO;
import com.andrew.model.Role;
import com.andrew.service.DeliveryService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/delivery")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DeliveryController {
    @Inject
    DeliveryService deliveryService;

    @POST
    @RequireRole({Role.OUTGROUP, Role.CAPTAIN})
    public Response createDelivery(@Valid DeliveryCreateDTO dto, @Context UriInfo uriInfo) {
        DeliveryResponseDTO created = deliveryService.createDelivery(dto);

        URI location = uriInfo.getAbsolutePathBuilder()
            .path(String.valueOf(created.id()))
            .build();

        return Response.created(location)
            .entity(created)
            .build();
    }

    @PUT
    @Path("{id}")
    @RequireRole({Role.OUTGROUP, Role.CAPTAIN})
    public Response editDelivery(@PathParam("id") Long id, @Valid DeliveryCreateDTO dto) {
        return Response.ok(deliveryService.updateDelivery(id, dto)).build();
    }

    @DELETE
    @Path("{id}")
    @RequireRole({Role.OUTGROUP, Role.CAPTAIN})
    public Response deleteDelivery(@PathParam("id") Long id) {
        deliveryService.deleteDelivery(id);
        return Response.ok().build();
    }
}
