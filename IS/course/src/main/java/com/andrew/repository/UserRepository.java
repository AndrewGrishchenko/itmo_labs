package com.andrew.repository;

import com.andrew.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserRepository {
    @Inject
    SessionFactory sessionFactory;

    public void save(User user) {
        sessionFactory.getCurrentSession().persist(user);
    }

    public List<User> getAll() {
        return sessionFactory.getCurrentSession().createQuery("from User", User.class).list();
    }

    public Optional<User> findById(Long id) {
        return sessionFactory.getCurrentSession().createQuery("from User u where u.id = :id", User.class)
                      .setParameter("id", id)
                      .uniqueResultOptional();
    }

    public Optional<User> findByUsername(String username) {
        return sessionFactory.getCurrentSession().createQuery("from User u where u.username = :username", User.class)
                      .setParameter("username", username)
                      .uniqueResultOptional();
    }

    public User update(User user) {
        return sessionFactory.getCurrentSession().merge(user);
    }

    public void delete(User user) {
        sessionFactory.getCurrentSession().remove(user);
    }
}
