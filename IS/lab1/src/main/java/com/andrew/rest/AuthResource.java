package com.andrew.rest;

import com.andrew.dto.OwnerResponse;
import com.andrew.model.Role;
import com.andrew.model.User;
import com.andrew.service.UserService;
import com.andrew.util.JwtUtil;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    UserService userService;

    public record AuthRequest(String username, String password) {}
    public record LoginResponse(OwnerResponse user, String token) {}

    @POST
    @Path("/register")
    public Response register(AuthRequest request) {
        User user = userService.register(request.username(), request.password(), Role.USER);
        return Response.ok(new OwnerResponse(user.getId(), user.getUsername())).build();
    }

    @POST
    @Path("/login")
    public Response login(AuthRequest request) {
        User user = userService.findByUsername(request.username())
                               .orElseThrow(() -> new NotFoundException("User with name " + request.username() + " not found"));
        String token = JwtUtil.generateToken(user.getUsername(), user.getId(), user.getRole().toString());
        return Response.ok(new LoginResponse(new OwnerResponse(user.getId(), user.getUsername()), token)).build();
    }
}
