package com.andrew.lab4.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.andrew.lab4.dto.AuthResponseDTO;
import com.andrew.lab4.dto.SignInDTO;
import com.andrew.lab4.dto.SignUpDTO;
import com.andrew.lab4.model.User;
import com.andrew.lab4.services.AccessTokenService;
import com.andrew.lab4.services.RefreshTokenService;
import com.andrew.lab4.services.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;

    public AuthController (UserService userService, AuthenticationConfiguration authenticationConfiguration, AccessTokenService accessTokenService, RefreshTokenService refreshTokenService) throws Exception {
        this.userService = userService;
        this.authenticationManager = authenticationConfiguration.getAuthenticationManager();
        this.accessTokenService = accessTokenService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register (@Validated @RequestBody SignUpDTO signUpDTO) {
        AuthResponseDTO response = new AuthResponseDTO();
        if (userService.existsByUsername(signUpDTO.getUsername())) {
            response.setStatus("Error");
            response.setMessage("User with this username already exists");
        } else if (userService.existsByEmail(signUpDTO.getEmail())) {
            response.setStatus("Error");
            response.setMessage("User with this email already exists");
        } else {
            userService.register(signUpDTO.getUsername(), signUpDTO.getEmail(), signUpDTO.getPassword());
            response.setStatus("Ok");
            response.setMessage("User registered successfully");
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login (@Validated @RequestBody SignInDTO signInDTO) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(signInDTO.getUsername(), signInDTO.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();

        AuthResponseDTO response = AuthResponseDTO.builder()
                                        .status("Ok")
                                        .message("Logged in successfully")
                                        .accessToken(accessTokenService.generateToken(signInDTO.getUsername()))
                                        .refreshToken(refreshTokenService.generateRefreshToken(signInDTO.getUsername()))
                                        .username(signInDTO.getUsername())
                                        .email(user.getEmail())
                                        .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<AuthResponseDTO> refreshToken (@Validated @RequestBody String refreshToken) {
        System.out.println("income: " + refreshToken);
        String newAccessToken = refreshTokenService.refreshAccessToken(refreshToken);
        System.out.println("new token: " + newAccessToken);
        if (newAccessToken != null) {
            return ResponseEntity.ok(AuthResponseDTO.builder()
                                        .status("Ok")
                                        .accessToken(newAccessToken)
                                        .message("Token refreshed successfully")
                                        .build());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(AuthResponseDTO.builder()
                                        .status("Error")
                                        .message("Invalid refresh token")
                                        .build());
        }
    }
}
