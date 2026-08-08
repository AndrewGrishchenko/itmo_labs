package com.andrew.security;

import java.lang.reflect.Method;
import java.util.Arrays;

import com.andrew.annotation.RequireRole;
import com.andrew.model.Role;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHORIZATION)
public class RoleCheckFilter implements ContainerRequestFilter {
    @Inject
    CurrentUser currentUser;

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        Method method = resourceInfo.getResourceMethod();
        RequireRole roleAnnotation = method.getAnnotation(RequireRole.class);

        if (roleAnnotation == null)
            roleAnnotation = resourceInfo.getResourceClass().getAnnotation(RequireRole.class);
        if (roleAnnotation == null)
            return;

        Role[] allowedRoles = roleAnnotation.value();

        if (currentUser.getUser() == null) {
            abortRequest(requestContext);
            return;
        }
        
        if (currentUser.getUser().getRole().equals(Role.ADMIN))
            return;
        
        if (Arrays.stream(allowedRoles).noneMatch(r -> r.equals(currentUser.getUser().getRole()))) {
            abortRequest(requestContext);
            return;
        }
    }

    private void abortRequest(ContainerRequestContext requestContext) {
        requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
            .entity("{\"error\":\"Forbidden\"}")
            .build());
    }
}
