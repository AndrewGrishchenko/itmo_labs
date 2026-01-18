package com.andrew.controller.mapper;

import java.util.HashMap;
import java.util.Map;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ForbiddenExceptionMapper implements ExceptionMapper<ForbiddenException> {
    @Override
    public Response toResponse(ForbiddenException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "forbidden");
        body.put("message", ex.getMessage());

        return Response.status(Response.Status.FORBIDDEN)
                       .entity(body)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}
