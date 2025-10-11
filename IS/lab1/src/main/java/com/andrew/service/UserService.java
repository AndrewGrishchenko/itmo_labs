package com.andrew.service;

import java.util.Optional;

import com.andrew.dao.UserDao;
import com.andrew.exceptions.ValidationException;
import com.andrew.model.Role;
import com.andrew.model.User;
import com.andrew.util.JwtUtil;
import com.andrew.util.PasswordUtil;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserService {

    @Inject
    UserDao userDao;

    public User register(String username, String password, Role role) {
        if (userDao.findByUsername(username).isPresent())
            throw new ValidationException("User " + username + " already exists");

        User user = new User(username, PasswordUtil.hash(password), role);
        userDao.save(user);
        return user;
    }

    public String login(String username, String password) {
        Optional<User> userOpt = userDao.findByUsername(username);

        if (userOpt.isEmpty() || !PasswordUtil.verify(password, userOpt.get().getPasswordHash()))
            throw new ValidationException("Invalid username or password");

        User user = userOpt.get();
        return JwtUtil.generateToken(user.getUsername(), user.getId(), user.getRole().toString());
    }

    public Optional<User> findById(Long id) {
        return userDao.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    public boolean authenticate(String username, String password) {
        return userDao.findByUsername(username)
            .map(user -> PasswordUtil.verify(password, user.getPasswordHash()))
            .orElse(false);
    }
}
