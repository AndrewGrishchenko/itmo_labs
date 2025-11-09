package com.andrew.rest;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.andrew.dto.PageResponse;
import com.andrew.dto.person.PersonFilter;
import com.andrew.dto.person.PersonRequest;
import com.andrew.dto.person.PersonResponse;
import com.andrew.model.Color;
import com.andrew.model.Country;
import com.andrew.model.Person;
import com.andrew.service.PersonService;
import com.andrew.util.ResponseMapper;
// import com.andrew.websocket.PersonSocketServer;
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
public class PersonResource {
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

        PageResponse<Person> pagedResult = personService.getAllPersons(mine, page, size, sort, order, filter);

        List<PersonResponse> responseList = pagedResult.content().stream()
            .map(ResponseMapper::toResponse)
            .collect(Collectors.toList());

        PageResponse<PersonResponse> response = new PageResponse<>(
            responseList,
            pagedResult.totalElements()
        );

        return Response.ok(response).build();
    }

    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") int id) {
        Person person = personService.getPersonById(id);
        return Response.ok(ResponseMapper.toResponse(person)).build();
    }

    @POST
    public Response create(@Valid PersonRequest request, @Context UriInfo uriInfo) {
        Person created = personService.createPerson(request);

        URI location = uriInfo.getAbsolutePathBuilder()
                              .path(String.valueOf(created.getId()))
                              .build();

        PersonResponse response = ResponseMapper.toResponse(created);

        // PersonSocketServer.broadcast(new WebSocketMessage<>("create", response));

        notifier.broadcast("person", new WebSocketMessage<>("create", response));

        return Response.created(location)
                       .entity(response)
                       .build();
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") int id, @Valid PersonRequest request) {
        Person updated = personService.updatePerson(id, request);
        PersonResponse response = ResponseMapper.toResponse(updated);
        // PersonSocketServer.broadcast(new WebSocketMessage<>("update", response));
        notifier.broadcast("person", new WebSocketMessage<>("update", response));
        return Response.ok(response).build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") int id) {
        personService.deletePerson(id);
        // PersonSocketServer.broadcast(new WebSocketMessage<>("delete", Map.of("id", id)));
        notifier.broadcast("person", new WebSocketMessage<>("delete", Map.of("id", id)));
        return Response.noContent().build();
    }
}
