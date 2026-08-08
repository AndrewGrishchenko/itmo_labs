package com.andrew.mapper.exception;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.StaleObjectStateException;
import org.hibernate.exception.LockAcquisitionException;

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
            if (cause instanceof OptimisticLockException || cause instanceof StaleObjectStateException)
                return buildConflictResponse("Optimistic Lock: object already modifying");

            if (cause instanceof LockAcquisitionException)
                return buildConflictResponse("Serialization Failure: concurrent transaction");

            if (cause instanceof SQLException) {
                String state = ((SQLException) cause).getSQLState();
                if ("40001".equals(state))
                    return buildConflictResponse("Serialization Failure: transaction aborted");
            }

            cause = cause.getCause();
        }
        
        return buildConflictResponse("Transaction Rollback failed");
    }

    private Response buildConflictResponse(String msg) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "conflict");
        body.put("message", msg);

        return Response.status(Response.Status.CONFLICT)
                       .entity(body)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}
