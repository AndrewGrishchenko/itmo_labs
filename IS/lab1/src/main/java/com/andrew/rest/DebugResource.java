package com.andrew.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import jakarta.inject.Inject;
import com.andrew.infra.HibernateProducer;

@Path("/debug")
public class DebugResource {

    @Inject
    HibernateProducer hp;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String check() {
        return "HibernateProducer created? " + (hp != null);
    }
}
