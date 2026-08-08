package com.andrew.controller;

import java.net.URI;

import com.andrew.annotation.RequireRole;
import com.andrew.dto.VisitRequest.VisitRequestCreateDTO;
import com.andrew.dto.VisitRequest.VisitRequestResponseDTO;
import com.andrew.model.Role;
import com.andrew.service.VisitRequestService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/visit")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VisitController {
    @Inject
    VisitRequestService visitRequestService;

    @GET
    @RequireRole({Role.CAPTAIN, Role.OUTGROUP, Role.KEEPER, Role.BOSS})
    public Response getAll() {
        return Response.ok(visitRequestService.getAll()).build();
    }

    @POST
    @RequireRole({Role.OUTGROUP, Role.CAPTAIN, Role.BOSS})
    public Response createVisitRequest(@Valid VisitRequestCreateDTO dto, @Context UriInfo uriInfo) {
        VisitRequestResponseDTO created = visitRequestService.createVisitRequest(dto);

        URI location = uriInfo.getAbsolutePathBuilder()
            .path(String.valueOf(created.id()))
            .build();

        return Response.created(location)
            .entity(created)
            .build();
    }

    @DELETE
    @Path("{id}")
    @RequireRole({Role.OUTGROUP, Role.CAPTAIN, Role.BOSS})
    public Response deleteVisitRequest(@PathParam("id") Long id) {
        visitRequestService.deleteVisitRequest(id);
        return Response.ok().build();
    }

    @POST
    @Path("{id}/submit")
    @RequireRole({Role.OUTGROUP, Role.CAPTAIN, Role.BOSS})
    public Response submitVisitRequest(@PathParam("id") Long id) {
        visitRequestService.submitVisitRequest(id);
        return Response.ok().build();
    }

    @POST
    @Path("{id}/approve")
    @RequireRole(Role.KEEPER)
    public Response approveVisitRequest(@PathParam("id") Long id) {
        visitRequestService.approveVisitRequest(id);
        return Response.ok().build();
    }

    @POST
    @Path("{id}/reject")
    @RequireRole(Role.KEEPER)
    public Response rejectVisitRequest(@PathParam("id") Long id) {
        visitRequestService.rejectVisitRequest(id);
        return Response.ok().build();
    }

    @POST
    @Path("{id}/complete")
    @RequireRole(Role.KEEPER)
    public Response completeVisitRequest(@PathParam("id") Long id) {
        visitRequestService.completeVisitRequest(id);
        return Response.ok().build();
    }
}
