package com.andrew.controller;

import java.net.URI;
import java.util.Map;

import com.andrew.dto.PageResponse;
import com.andrew.dto.person.PersonFilter;
import com.andrew.dto.person.PersonRequest;
import com.andrew.dto.person.PersonResponse;
import com.andrew.model.Color;
import com.andrew.model.Country;
import com.andrew.service.PersonService;
import com.andrew.websocket.WebSocketMessage;
import com.andrew.websocket.WebSocketNotifier;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/person")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonController {
    @Inject
    PersonService personService;

    @Inject
    private WebSocketNotifier notifier;

    @GET
    public Response getAll(
        @QueryParam("mine") @DefaultValue("false") boolean mine,
        @QueryParam("page") @DefaultValue("0") int page,
        @QueryParam("size") @DefaultValue("10") int size,
        @QueryParam("sort") @DefaultValue("id") String sort,
        @QueryParam("order") @DefaultValue("asc") String order,

        @QueryParam("owner.id") Long ownerId,
        @QueryParam("id") Long id,
        @QueryParam("name") String name,
        @QueryParam("eyeColor") Color eyeColor,
        @QueryParam("hairColor") Color hairColor,
        @QueryParam("location") Long locationId,
        @QueryParam("weight") Float weight,
        @QueryParam("nationality") Country nationality
    ) {
        PersonFilter filter = new PersonFilter(
            ownerId,
            id,
            name,
            eyeColor,
            hairColor,
            locationId,
            weight,
            nationality
        );

        PageResponse<PersonResponse> response = personService.getAllPersons(mine, page, size, sort, order, filter);

        return Response.ok(response).build();
    }

    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") Long id) {
        PersonResponse person = personService.getPersonById(id);
        return Response.ok(person).build();
    }

    @POST
    public Response create(@Valid PersonRequest request, @Context UriInfo uriInfo) {
        PersonResponse created = personService.createPerson(request);

        URI location = uriInfo.getAbsolutePathBuilder()
                              .path(String.valueOf(created.id()))
                              .build();

        notifier.broadcast("person", new WebSocketMessage<>("create", created));

        return Response.created(location)
                       .entity(created)
                       .build();
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") Long id, @Valid PersonRequest request) {
        PersonResponse updated = personService.updatePerson(id, request);
        notifier.broadcast("person", new WebSocketMessage<>("update", updated));
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") Long id) {
        personService.deletePerson(id);
        notifier.broadcast("person", new WebSocketMessage<>("delete", Map.of("id", id)));
        return Response.noContent().build();
    }
}
