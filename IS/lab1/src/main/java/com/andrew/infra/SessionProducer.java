package com.andrew.infra;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import jakarta.inject.Inject;

@RequestScoped
public class SessionProducer {

    @Inject
    private SessionFactory sessionFactory;

    @Produces
    @RequestScoped
    public Session produceSession() {
        return sessionFactory.openSession();
    }

    public void close(@Disposes Session session) {
        if (session != null && session.isOpen()) {
            session.close();
        }
    }
}
