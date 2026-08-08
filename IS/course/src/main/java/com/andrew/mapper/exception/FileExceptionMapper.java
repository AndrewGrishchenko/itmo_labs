package com.andrew.mapper.exception;

import java.util.HashMap;
import java.util.Map;

import com.andrew.exceptions.FileException;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class FileExceptionMapper implements ExceptionMapper<FileException> {
    @Override
    public Response toResponse(FileException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "file_error");
        body.put("message", ex.getMessage());

        return Response.status(Response.Status.BAD_REQUEST)
            .entity(body)
            .type(MediaType.APPLICATION_JSON)
            .build();
    }
}