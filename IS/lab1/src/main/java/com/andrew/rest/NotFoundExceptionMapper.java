package com.andrew.rest;

import java.util.HashMap;
import java.util.Map;

import com.andrew.exceptions.NotFoundException;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
    @Override
    public Response toResponse(NotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "not_found");
        body.put("message", ex.getMessage());

        return Response.status(Response.Status.NOT_FOUND)
                       .entity(body)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}
