package com.andrew.lab1.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.andrew.lab1.entity.XmlUser;
import com.andrew.lab1.entity.XmlUserDetails;
import com.andrew.lab1.exception.NotFoundException;
import com.andrew.lab1.repository.XmlUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class XmlUserDetailsService implements UserDetailsService {
    private final XmlUserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        XmlUser user = repository.findByUsername(username)
            .orElseThrow(() -> new NotFoundException("User with username " + username + " not found"));

        return new XmlUserDetails(user);
    }
}
