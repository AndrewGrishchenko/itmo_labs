package com.andrew.controller;

import java.net.URI;

import com.andrew.annotation.RequireRole;
import com.andrew.dto.RequirementCondition.RequirementConditionCreateDTO;
import com.andrew.dto.RequirementCondition.RequirementConditionResponseDTO;
import com.andrew.dto.requirement.RequirementCreateDTO;
import com.andrew.dto.requirement.RequirementResponseDTO;
import com.andrew.model.Role;
import com.andrew.service.RequirementConditionService;
import com.andrew.service.RequirementService;

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

@Path("/requirement")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RequirementController {
    @Inject
    RequirementService requirementService;

    @Inject
    RequirementConditionService requirementConditionService;

    @GET
    @RequireRole({Role.OUTGROUP, Role.KEEPER, Role.BOSS})
    public Response getRequirements() {
        return Response.ok(requirementService.getAll()).build();
    }

    @POST
    @RequireRole(Role.OUTGROUP)
    public Response createRequirement(@Valid RequirementCreateDTO dto, @Context UriInfo uriInfo) {
        RequirementResponseDTO created = requirementService.createRequirement(dto);

        URI location = uriInfo.getAbsolutePathBuilder()
            .path(String.valueOf(created.id()))
            .build();

        return Response.created(location)
            .entity(created)
            .build();
    }

    @GET
    @Path("{id}")
    @RequireRole({Role.OUTGROUP, Role.KEEPER, Role.BOSS})
    public Response getConditions(@PathParam("id") Long id) {
        return Response.ok(requirementConditionService.getByRequirement(id)).build();
    }

    @PUT
    @Path("{id}")
    @RequireRole(Role.OUTGROUP)
    public Response editRequirement(@PathParam("id") Long id, @Valid RequirementCreateDTO dto) {
        return Response.ok(requirementService.editRequirement(id, dto)).build();
    }

    @DELETE
    @Path("{id}")
    @RequireRole(Role.OUTGROUP)
    public Response deleteRequirement(@PathParam("id") Long id) {
        requirementService.deleteRequirement(id);
        return Response.ok().build();
    }

    @POST
    @Path("/condition")
    @RequireRole(Role.OUTGROUP)
    public Response createCondition(@Valid RequirementConditionCreateDTO dto, @Context UriInfo uriInfo) {
        RequirementConditionResponseDTO created = requirementConditionService.createRequirementCondition(dto);

        URI location = uriInfo.getAbsolutePathBuilder()
            .path(String.valueOf(created.id()))
            .build();

        return Response.created(location)
            .entity(created)
            .build();
    }

    @PUT
    @Path("/condition/{id}")
    @RequireRole(Role.OUTGROUP)
    public Response editCondition(@PathParam("id") Long id, @Valid RequirementConditionCreateDTO dto) {
        return Response.ok(requirementConditionService.updateRequirementCondition(id, dto)).build();
    }

    @DELETE
    @Path("/condition/{id}")
    @RequireRole(Role.OUTGROUP)
    public Response deleteCondition(@PathParam("id") Long id) {
        requirementConditionService.deleteRequirementCondition(id);
        return Response.ok().build();
    }

    @POST
    @Path("/{id}/submit")
    @RequireRole(Role.OUTGROUP)
    public Response submitRequirement(@PathParam("id") Long id) {
        requirementService.submitRequirement(id);
        return Response.ok().build();
    }
}
