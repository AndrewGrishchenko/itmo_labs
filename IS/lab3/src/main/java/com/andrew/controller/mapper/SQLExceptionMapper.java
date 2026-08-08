package com.andrew.controller.mapper;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class SQLExceptionMapper implements ExceptionMapper<PersistenceException> {
    @Override
    public Response toResponse(PersistenceException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "sql_exception");
        body.put("message", ex.getMessage());

        return Response.status(Response.Status.BAD_REQUEST)
                       .entity(body)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}
