package com.andrew.service;

import java.util.List;
import java.util.Optional;

import com.andrew.dto.PageResponse;
import com.andrew.exceptions.ValidationException;
import com.andrew.model.Role;
import com.andrew.model.User;
import com.andrew.repository.UserRepository;
import com.andrew.security.CurrentUser;
import com.andrew.util.JwtUtil;
import com.andrew.util.PasswordUtil;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UserService {
    @Inject
    UserRepository userRepository;

    @Inject
    CurrentUser currentUser;

    @Transactional
    public User register(String username, String password, Role role) {
        if (userRepository.findByUsername(username).isPresent())
            throw new ValidationException("User " + username + " already exists");

        User user = new User(username, PasswordUtil.hash(password), role);
        userRepository.save(user);
        return user;
    }

    @Transactional
    public String login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty() || !PasswordUtil.verify(password, userOpt.get().getPasswordHash()))
            throw new ValidationException("Invalid username or password");

        User user = userOpt.get();
        return JwtUtil.generateToken(user.getUsername(), user.getId(), user.getRole().toString());
    }

    @Transactional
    public PageResponse<User> getAllUsers(boolean mine, int page, int size, String sort, String order) {
        return mine ?
            new PageResponse<>(List.of(currentUser.getUser()), 1) :
            userRepository.findAllPaginatedAndSorted(page, size, sort, order);
    }

    @Transactional
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean authenticate(String username, String password) {
        return userRepository.findByUsername(username)
            .map(user -> PasswordUtil.verify(password, user.getPasswordHash()))
            .orElse(false);
    }
}
