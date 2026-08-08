package com.andrew.lab1.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.andrew.lab1.dto.user.UserCreateRequest;
import com.andrew.lab1.dto.user.UserResponse;
import com.andrew.lab1.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@RequestBody @Valid UserCreateRequest request) {
        return userService.createUser(request);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public Page<UserResponse> getAll(Pageable pageable) {
        return userService.getAll(pageable);
    }

    @GetMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public UserResponse getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PutMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public UserResponse updateUser(@PathVariable Long id, @RequestBody @Valid UserCreateRequest request) {
        return userService.updateUser(id, request);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
