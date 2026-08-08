package com.andrew.controller;

import java.net.URI;

import com.andrew.annotation.RequireRole;
import com.andrew.dto.IncidentReport.IncidentReportCreateDTO;
import com.andrew.dto.IncidentReport.IncidentReportResponseDTO;
import com.andrew.dto.fine.FineCreateDTO;
import com.andrew.model.Role;
import com.andrew.service.IncidentReportService;

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

@Path("/incident")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IncidentController {
    @Inject
    IncidentReportService incidentReportService;

    @GET
    @RequireRole({Role.KEEPER, Role.BOSS})
    public Response getAll() {
        return Response.ok(incidentReportService.getAll()).build();
    }

    @POST
    @RequireRole(Role.KEEPER)
    public Response createIncidentReport(@Valid IncidentReportCreateDTO dto, @Context UriInfo uriInfo) {
        IncidentReportResponseDTO created = incidentReportService.createIncidentReport(dto);

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
    public Response closeIncident(@PathParam("id") Long id) {
        incidentReportService.closeIncident(id);
        return Response.ok().build();
    }

    @POST
    @Path("{id}/fine")
    @RequireRole(Role.BOSS)
    public Response fineIncident(@PathParam("id") Long id, FineCreateDTO dto) {
        incidentReportService.fineIncident(id, dto);
        return Response.ok().build();
    }
}
