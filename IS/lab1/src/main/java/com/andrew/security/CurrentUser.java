package com.andrew.security;


import com.andrew.model.Role;
import com.andrew.model.User;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class CurrentUser {
    private User user;

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        if (user == null) throw new IllegalStateException("No logged-in user");
        return user;
    }

    public boolean isAdmin() {
        return user.getRole().equals(Role.ADMIN);
    }
}
