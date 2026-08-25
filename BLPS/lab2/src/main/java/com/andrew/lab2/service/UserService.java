package com.andrew.lab2.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.andrew.lab2.dto.user.UserCreateRequest;
import com.andrew.lab2.dto.user.UserLoginRequest;
import com.andrew.lab2.dto.user.UserLoginResponse;
import com.andrew.lab2.dto.user.UserResponse;
import com.andrew.lab2.entity.enums.Role;
import com.andrew.lab2.entity.xml.XmlUser;
import com.andrew.lab2.exception.ForbiddenException;
import com.andrew.lab2.exception.NotFoundException;
import com.andrew.lab2.exception.ValidationException;
import com.andrew.lab2.repository.xml.XmlUserRepository;
import com.andrew.lab2.util.JwtUtil;
import com.andrew.lab2.util.ResponseMapper;
import com.andrew.lab2.util.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final XmlUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent())
            throw new ValidationException("User " + request.username() + " already exists");

        XmlUser user = new XmlUser();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.AUTHOR);

        return ResponseMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public UserLoginResponse login(UserLoginRequest request) {
        XmlUser user = userRepository.findByUsername(request.username())
            .orElseThrow(() -> new NotFoundException("User not found"));

        if (!passwordEncoder.matches(request.password(), user.getPassword()))
            throw new ForbiddenException("Wrong password");

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        return new UserLoginResponse(token, user.getRole());
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getAll(Pageable pageable) {
        System.out.println("TX active: " +
            TransactionSynchronizationManager.isActualTransactionActive());

        System.out.println("TX name: " +
            TransactionSynchronizationManager.getCurrentTransactionName());

        return userRepository.findAll(pageable).map(ResponseMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        return ResponseMapper.toResponse(userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User", id)));
    }

    @Transactional
    public UserResponse updateUser(Long id, UserCreateRequest request) {
        XmlUser existing = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User", id));

        if (!SecurityUtils.getCurrentUserId().equals(id))
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
        XmlUser existing = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User", id));

        if (!SecurityUtils.getCurrentUserId().equals(id))
            throw new ForbiddenException();

        userRepository.delete(existing);
    }

    // @Transactional
    // private XmlUser getCurrentUser() {
    //     Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    //     return userRepository.findByUsername(auth.getName())
    //         .orElseThrow(() -> new NotFoundException("User with username " + auth.getName() + " not found"));
    // }
}
