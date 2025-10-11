package com.andrew.infra;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

@ApplicationScoped
public class HibernateProducer {

    private SessionFactory sessionFactory;

    @Produces
    @ApplicationScoped
    public SessionFactory produceSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = new Configuration()
                .configure()
                .addAnnotatedClass(com.andrew.model.User.class)
                .buildSessionFactory();
        }
        return sessionFactory;
    }
}
