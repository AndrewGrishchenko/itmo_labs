package com.andrew.controller.mapper;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.StaleObjectStateException;

import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.RollbackException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class RollbackExceptionMapper implements ExceptionMapper<RollbackException> {
    @Override
    public Response toResponse(RollbackException ex) {
        Throwable cause = ex.getCause();

        while (cause != null) {
            if (cause instanceof OptimisticLockException || cause instanceof StaleObjectStateException) {
                Map<String, Object> body = new HashMap<>();
                body.put("error", "optimistic_lock");
                body.put("message", "Object is already modifying");

                return Response.status(Response.Status.CONFLICT)
                               .entity(body)
                               .type(MediaType.APPLICATION_JSON)
                               .build();
            }
            cause = cause.getCause();
        }
        
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                       .entity("Transaction Rollback failed")
                       .build();
    }
}
