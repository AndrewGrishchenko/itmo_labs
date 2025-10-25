package com.andrew.rest.mapper;

import java.util.HashMap;
import java.util.Map;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    @Override
    public Response toResponse(ConstraintViolationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "constraint_violation");
        body.put("message", ex.getMessage());

        return Response.status(Response.Status.BAD_REQUEST)
                       .entity(body)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}
