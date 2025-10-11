package com.andrew.security;

import com.andrew.model.User;
import com.andrew.service.UserService;
import com.andrew.util.JwtUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtAuthFilter implements ContainerRequestFilter {

    @Inject
    private CurrentUser currentUser;

    @Inject
    private UserService userService;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String path = requestContext.getUriInfo().getPath();
        if (path.equals("/auth/login") || path.equals("/auth/register")) {
            return;
        }
        
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            abortRequest(requestContext, "Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring("Bearer ".length());
        Claims claims;
        try {
            claims = JwtUtil.validateToken(token);
        } catch (JwtException e) {
            abortRequest(requestContext, "Invalid token: " + e.getMessage());
            return;
        }

        Long userId = claims.get("userId", Long.class);
        
        User user = userService.findById(userId).orElse(null);

        if (user == null) {
            abortRequest(requestContext, "User not found or token mismatch");
            return;
        }

        currentUser.setUser(user);
    }

    private void abortRequest(ContainerRequestContext requestContext, String message) {
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\":\"" + message + "\"}")
                        .build()
        );
    }
}
