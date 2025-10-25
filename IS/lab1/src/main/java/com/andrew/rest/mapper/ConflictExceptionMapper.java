package com.andrew.rest.mapper;

import java.util.HashMap;
import java.util.Map;

import com.andrew.exceptions.ConflictException;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ConflictExceptionMapper implements ExceptionMapper<ConflictException> {
    @Override
    public Response toResponse(ConflictException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", ex.getMessage());

        if (ex.getDependencies() != null) {
            Map<String, Object> dependencyCount = new HashMap<>();
            ex.getDependencies().forEach((key, value) -> {
                dependencyCount.put(key, value);
            });
            body.put("dependencyCount", dependencyCount);
        }

        return Response.status(Response.Status.CONFLICT)
                       .entity(body)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}
