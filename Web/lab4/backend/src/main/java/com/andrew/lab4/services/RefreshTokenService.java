package com.andrew.lab4.services;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Service
public class RefreshTokenService {
    private final AccessTokenService accessTokenService;
    
    public RefreshTokenService (AccessTokenService accessTokenService) {
        this.accessTokenService = accessTokenService;
    }

    @Value("${jwt.refreshSecretKey}")
    private String SECRET_KEY;
    
    @Value("${auth.refreshTokenValidity}")
    private Duration REFRESH_TOKEN_VALIDITY;

    @Value("${jwt.secretKey}")
    private String refreshSecretKey;

    private SecretKey refreshSecretKeyObject;

    @PostConstruct
    public void init() {
        refreshSecretKeyObject = Keys.hmacShaKeyFor(refreshSecretKey.getBytes());
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plusSeconds(REFRESH_TOKEN_VALIDITY.toSeconds()))) 
                .signWith(refreshSecretKeyObject)
                .compact();
    }

    public boolean validateRefreshToken(String refreshToken) {
        try {

            Jwts.parser().verifyWith(refreshSecretKeyObject).build().parseSignedClaims(refreshToken);
            return true;
        } catch (JwtException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public String refreshAccessToken(String refreshToken) {
        if (validateRefreshToken(refreshToken)) {
            String username = extractUsername(refreshToken);
            return accessTokenService.generateToken(username);
        }
        return null;
    }

    public String extractUsername(String refreshToken) {
        return Jwts.parser()
                .verifyWith(refreshSecretKeyObject)
                .build()
                .parseSignedClaims(refreshToken)
                .getPayload()
                .getSubject();
    }
}
