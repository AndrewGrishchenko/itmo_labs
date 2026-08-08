package com.andrew.lab1.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.andrew.lab1.dto.user.UserCreateRequest;
import com.andrew.lab1.dto.user.UserResponse;
import com.andrew.lab1.entity.User;
import com.andrew.lab1.entity.enums.Role;
import com.andrew.lab1.exception.ForbiddenException;
import com.andrew.lab1.exception.NotFoundException;
import com.andrew.lab1.exception.ValidationException;
import com.andrew.lab1.repository.UserRepository;
import com.andrew.lab1.util.ResponseMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent())
            throw new ValidationException("User " + request.username() + " already exists");

        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.AUTHOR);

        return ResponseMapper.toResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(ResponseMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        return ResponseMapper.toResponse(userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User", id)));
    }

    @Transactional
    public UserResponse updateUser(Long id, UserCreateRequest request) {
        User existing = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User", id));

        if (!getCurrentUser().getId().equals(id))
            throw new ForbiddenException();

        if (!existing.getUsername().equals(request.username())) {
            if (userRepository.findByUsername(request.username()).isPresent())
                throw new ForbiddenException("Username already taken");
        }

        existing.setUsername(request.username());
        existing.setPassword(passwordEncoder.encode(request.password()));

        return ResponseMapper.toResponse(userRepository.save(existing));
    }

    @Transactional
    public void deleteUser(Long id) {
        User existing = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User", id));

        if (!getCurrentUser().getId().equals(id))
            throw new ForbiddenException();

        userRepository.delete(existing);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new NotFoundException("User with username " + username + " not found"));

        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(auth.getName())
            .orElseThrow(() -> new NotFoundException("User with username " + auth.getName() + " not found"));
    }
}
