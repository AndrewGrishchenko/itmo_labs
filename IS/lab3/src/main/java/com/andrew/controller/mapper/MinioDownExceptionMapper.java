package com.andrew.controller.mapper;

import java.util.HashMap;
import java.util.Map;

import com.andrew.exceptions.MinioDownException;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class MinioDownExceptionMapper implements ExceptionMapper<MinioDownException> {
    @Override
    public Response toResponse(MinioDownException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "minio_err");
        body.put("message", ex.getMessage());

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                       .entity(body)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}
