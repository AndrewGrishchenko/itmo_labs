package com.andrew.rest;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import com.andrew.dto.OwnerResponse;
import com.andrew.dto.person.PersonRequest;
import com.andrew.dto.person.PersonResponse;
import com.andrew.model.Person;
import com.andrew.service.PersonService;

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

    @GET
    public Response getAll(
        @QueryParam("mine") @DefaultValue("false") boolean mine
    ) {
        List<Person> list = personService.getAllPersons(mine);

        List<PersonResponse> responseList = list.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());

        return Response.ok(responseList).build();
    }

    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") int id) {
        Person person = personService.getPersonById(id);
        return Response.ok(toResponse(person)).build();
    }

    @POST
    public Response create(@Valid PersonRequest request, @Context UriInfo uriInfo) {
        Person created = personService.createPerson(request);

        URI location = uriInfo.getAbsolutePathBuilder()
                              .path(String.valueOf(created.getId()))
                              .build();

        return Response.created(location)
                       .entity(toResponse(created))
                       .build();
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") int id, @Valid PersonRequest request) {
        Person updated = personService.updatePerson(id, request);
        return Response.ok(toResponse(updated)).build();
    }

    @DELETE
    @Path("id")
    public Response delete(@PathParam("id") int id) {
        personService.deletePerson(id);
        return Response.noContent().build();
    }
 
    private PersonResponse toResponse(Person entity) {
        OwnerResponse owner = new OwnerResponse(
            entity.getOwner().getId(),
            entity.getOwner().getUsername()
        );

        return new PersonResponse(
            entity.getId(),
            owner,
            entity.getName(),
            entity.getEyeColor(),
            entity.getHairColor(),
            entity.getLocation(),
            entity.getWeight(),
            entity.getNationality()
        );
    }
}
