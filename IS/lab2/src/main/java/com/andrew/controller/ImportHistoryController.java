package com.andrew.controller;

import com.andrew.dto.PageResponse;
import com.andrew.dto.import_history.ImportHistoryFilter;
import com.andrew.dto.import_history.ImportHistoryResponse;
import com.andrew.model.OperationStatus;
import com.andrew.service.ImportHistoryService;

import jakarta.inject.Inject;
import jakarta.websocket.server.PathParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/history")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ImportHistoryController {
    @Inject
    private ImportHistoryService importHistoryService;

    @GET
    public Response getAll(
        @QueryParam("page") @DefaultValue("0") int page,
        @QueryParam("size") @DefaultValue("10") int size,
        @QueryParam("sort") @DefaultValue("id") String sort,
        @QueryParam("order") @DefaultValue("asc") String order,

        @QueryParam("id") Long id,
        @QueryParam("user.id") Long userId,
        @QueryParam("operationStatus") OperationStatus operationStatus,
        @QueryParam("objectCount") Integer objectCount
    ) {
        ImportHistoryFilter filter = new ImportHistoryFilter(id, userId, operationStatus, objectCount);

        PageResponse<ImportHistoryResponse> response = importHistoryService.getAllImportHistory(page, size, sort, order, filter);

        return Response.ok(response).build();
    }

    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") int id) {
        ImportHistoryResponse importHistory = importHistoryService.getById(id);
        return Response.ok(importHistory).build();
    }
}
