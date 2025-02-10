package com.andrew.lab4.services;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Service
public class AccessTokenService {
    @Value("${jwt.secretKey}")
    private String SECRET_KEY;

    private SecretKey secretKey;

    @Value("${auth.accessTokenValidity}")
    private Duration accessTokenValidity;

    @PostConstruct
    public void init() {
        secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String extractUsername(String token) {
        try {
            return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        } catch (MalformedJwtException e) {
            return null;
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        Claims claims = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
        
        return claims.getSubject().equals(userDetails.getUsername()) && 
            claims.getExpiration().after(new Date());
    }

    public String generateToken (String username) {
        return Jwts.builder()
            .subject(username)
            .issuedAt(new Date())
            .expiration(Date.from(Instant.now().plusSeconds(accessTokenValidity.toSeconds())))
            .signWith(secretKey)
            .compact();
    }
}
