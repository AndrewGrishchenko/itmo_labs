package com.andrew.controller;

import com.andrew.annotation.RequireRole;
import com.andrew.dto.supply.SupplyEditDTO;
import com.andrew.model.Role;
import com.andrew.service.SupplyService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/supply")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SupplyController {
    @Inject
    SupplyService supplyService;

    @GET
    @RequireRole({Role.KEEPER, Role.BOSS})
    public Response getAll() {
        return Response.ok(supplyService.getAll()).build();
    }

    @PUT
    @RequireRole(Role.KEEPER)
    public Response editSupply(@Valid SupplyEditDTO dto) {
        return Response.ok(supplyService.editSupply(dto)).build();
    }
}
