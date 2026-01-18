package com.andrew.controller.mapper;

import java.util.HashMap;
import java.util.Map;

import jakarta.json.bind.JsonbException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class JsonbExceptionMapper implements ExceptionMapper<JsonbException> {
    @Override
    public Response toResponse(JsonbException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "json_parse_error");
        body.put("message", ex.getMessage());

        return Response.status(Response.Status.BAD_REQUEST)
                       .entity(body)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}
