package com.andrew.dao;

import com.andrew.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.hibernate.Session;

import java.util.Optional;

@ApplicationScoped
public class UserDao {
    @Inject
    Session session;

    public void save(User user) {
        session.beginTransaction();
        session.persist(user);
        session.getTransaction().commit();
    }

    public Optional<User> findById(Long id) {
        return session.createQuery("from User u where u.id = :id", User.class)
                      .setParameter("id", id)
                      .uniqueResultOptional();
    }

    public Optional<User> findByUsername(String username) {
        return session.createQuery("from User u where u.username = :username", User.class)
                      .setParameter("username", username)
                      .uniqueResultOptional();
    }

    public void delete(User user) {
        session.beginTransaction();
        session.remove(user);
        session.getTransaction().commit();
    }
}
