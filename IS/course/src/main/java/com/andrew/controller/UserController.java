package com.andrew.controller;

import com.andrew.annotation.RequireRole;
import com.andrew.dto.user.UserCreateDTO;
import com.andrew.dto.user.UserCreateResponseDTO;
import com.andrew.dto.user.UserLoginDTO;
import com.andrew.dto.user.UserLoginResponseDTO;
import com.andrew.dto.user.UserUpdateDTO;
import com.andrew.exceptions.NotFoundException;
import com.andrew.model.Role;
import com.andrew.service.UserService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {
    @Inject
    UserService userService;

    @POST
    @Path("/register")
    @RequireRole(Role.ADMIN)
    public Response register(@Valid UserCreateDTO request) {
        UserCreateResponseDTO response = userService.register(request);
        return Response.ok(response).build();
    }

    @POST
    @Path("/login")
    public Response login(@Valid UserLoginDTO request) {
        userService.findByUsername(request.username())
                               .orElseThrow(() -> new NotFoundException("User with name " + request.username() + " not found"));
        
        UserLoginResponseDTO response = userService.login(request);
        return Response.ok(response).build();
    }

    @PUT
    @Path("/{id}")
    @RequireRole(Role.ADMIN)
    public Response editUser(@PathParam("id") Long id, @Valid UserUpdateDTO dto) {
        return Response.ok(userService.editUser(id, dto)).build();
    }

    @DELETE
    @Path("/{id}")
    @RequireRole(Role.ADMIN)
    public Response deleteUser(@PathParam("id") Long id) {
        userService.deleteUser(id);
        return Response.ok().build();
    }

    @GET
    @Path("/users")
    @RequireRole({Role.KEEPER, Role.ADMIN})
    public Response getAll() {
        return Response.ok(userService.getAll()).build();
    }
}
