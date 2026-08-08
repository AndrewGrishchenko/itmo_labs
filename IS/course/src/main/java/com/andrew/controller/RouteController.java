package com.andrew.controller;

import java.net.URI;

import com.andrew.annotation.RequireRole;
import com.andrew.dto.RouteRequest.RouteRequestCreateDTO;
import com.andrew.dto.RouteRequest.RouteRequestResponseDTO;
import com.andrew.dto.route.RouteBuildDTO;
import com.andrew.dto.route.RouteCreateDTO;
import com.andrew.dto.route.RouteResponseDTO;
import com.andrew.model.Role;
import com.andrew.service.RouteManagementService;
import com.andrew.service.RouteRequestService;
import com.andrew.service.RouteService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
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

@Path("/route")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RouteController {
    @Inject
    RouteRequestService routeRequestService;

    @Inject
    RouteService routeService;

    @Inject
    RouteManagementService routeManagementService;

    @GET
    @Path("request")
    @RequireRole({Role.CAPTAIN, Role.KEEPER, Role.BOSS})
    public Response getRouteRequests() {
        return Response.ok(routeRequestService.getAll()).build();
    }

    @POST
    @Path("request")
    @RequireRole(Role.CAPTAIN)
    public Response requestRoute(@Valid RouteRequestCreateDTO dto, @Context UriInfo uriInfo) {
        RouteRequestResponseDTO created = routeRequestService.createRouteRequest(dto);

        URI location = uriInfo.getAbsolutePathBuilder()
            .path(String.valueOf(created.id()))
            .build();
        
        return Response.created(location)
            .entity(created)
            .build();
    }

    @PUT
    @Path("request/{id}")
    @RequireRole(Role.CAPTAIN)
    public Response editRouteRequest(@PathParam("id") Long id, @Valid RouteRequestCreateDTO dto) {
        return Response.ok(routeRequestService.updateRouteRequest(id, dto)).build();
    }

    @POST
    @Path("request/{id}/submit")
    @RequireRole(Role.CAPTAIN)
    public Response submitRouteRequest(@PathParam("id") Long id) {
        routeManagementService.submitRouteRequest(id);
        return Response.ok().build();
    }

    @GET
    @RequireRole({Role.CAPTAIN, Role.KEEPER, Role.BOSS})
    public Response getRoutes() {
        return Response.ok(routeService.getAll()).build();
    }

    @POST
    @RequireRole(Role.KEEPER)
    public Response createRoute(@Valid RouteCreateDTO dto, @Context UriInfo uriInfo) {
        RouteResponseDTO created = routeService.createRoute(dto);

        URI location = uriInfo.getAbsolutePathBuilder()
            .path(String.valueOf(created.id()))
            .build();

        return Response.created(location)
            .entity(created)
            .build();
    }

    @POST
    @Path("{id}/segments")
    @RequireRole(Role.KEEPER)
    public Response buildRoute(@PathParam("id") Long id, @Valid RouteBuildDTO dto) {
        routeManagementService.buildRoute(id, dto);
        return Response.ok().build();
    }

    @POST
    @Path("{id}/approve")
    @RequireRole(Role.KEEPER)
    public Response approveRoute(@PathParam("id") Long id) {
        routeManagementService.approveRoute(id);
        return Response.ok().build();
    }

    @POST
    @Path("request/{id}/reject")
    @RequireRole(Role.KEEPER)
    public Response rejectRouteByKeeper(@PathParam("id") Long id) {
        routeManagementService.rejectRouteByKeeper(id);
        return Response.ok().build();
    }

    @POST
    @Path("{id}/start")
    @RequireRole(Role.CAPTAIN)
    public Response startRoute(@PathParam("id") Long id) {
        routeManagementService.startRoute(id);
        return Response.ok().build();
    }

    @POST
    @Path("{id}/reject")
    @RequireRole(Role.CAPTAIN)
    public Response rejectRouteRequest(@PathParam("id") Long id) {
        routeManagementService.rejectRouteRequestByCaptain(id);
        return Response.ok().build();
    }

    @POST
    @Path("{id}/complete")
    @RequireRole(Role.CAPTAIN)
    public Response completeRoute(@PathParam("id") Long id) {
        routeManagementService.completeRoute(id);
        return Response.ok().build();
    }
}
