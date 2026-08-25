package com.andrew.lab2.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.andrew.lab2.entity.xml.XmlUser;
import com.andrew.lab2.entity.xml.XmlUserDetails;
import com.andrew.lab2.exception.NotFoundException;
import com.andrew.lab2.repository.xml.XmlUserRepository;

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
