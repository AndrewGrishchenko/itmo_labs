package com.andrew.infra;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

@ApplicationScoped
public class SessionFactoryProducer {
    private SessionFactory sessionFactory;

    @Produces
    @ApplicationScoped
    public SessionFactory produceSessionFactory() {
        if (sessionFactory == null) {
            Configuration configuration = new Configuration().configure();
            this.sessionFactory = configuration.buildSessionFactory();
        }
        return sessionFactory;
    }

    public void close(@Disposes SessionFactory sessionFactory) {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}