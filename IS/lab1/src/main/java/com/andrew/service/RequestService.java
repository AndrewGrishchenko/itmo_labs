package com.andrew.service;

import com.andrew.security.CurrentUser;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RequestService {
    @Inject
    CurrentUser currentUser;
}
