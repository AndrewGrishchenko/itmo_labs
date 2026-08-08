package com.andrew.controller;

import java.net.URI;

import com.andrew.annotation.RequireRole;
import com.andrew.dto.complaint.ComplaintCreateDTO;
import com.andrew.dto.complaint.ComplaintResponseDTO;
import com.andrew.dto.fine.FineCreateDTO;
import com.andrew.model.Role;
import com.andrew.service.ComplaintService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/complaint")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ComplaintController {
    @Inject
    ComplaintService complaintService;

    @GET
    @RequireRole({Role.CAPTAIN, Role.KEEPER, Role.BOSS})
    public Response getAll() {
        return Response.ok(complaintService.getAll()).build();
    }

    @POST
    @RequireRole(Role.CAPTAIN)
    public Response createComplaint(@Valid ComplaintCreateDTO dto, @Context UriInfo uriInfo) {
        ComplaintResponseDTO created = complaintService.createComplaint(dto);

        URI location = uriInfo.getAbsolutePathBuilder()
            .path(String.valueOf(created.id()))
            .build();

        return Response.created(location)
            .entity(created)
            .build();
    }

    @POST
    @Path("{id}/close")
    @RequireRole(Role.BOSS)
    public Response closeComplaint(@PathParam("id") Long id) {
        complaintService.closeComplaint(id);
        return Response.ok().build();
    }

    @POST
    @Path("{id}/fine")
    @RequireRole(Role.BOSS)
    public Response fineComplaint(@PathParam("id") Long id, FineCreateDTO dto) {
        complaintService.fineComplaint(id, dto);
        return Response.ok().build();
    }
}
