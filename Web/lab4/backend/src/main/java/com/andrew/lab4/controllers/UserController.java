package com.andrew.lab4.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.andrew.lab4.dto.PointRequestDTO;
import com.andrew.lab4.dto.PointResponseDTO;
import com.andrew.lab4.model.User;
import com.andrew.lab4.services.UserService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    public UserController (UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/getPoints")
    public ResponseEntity<List<PointResponseDTO>> getPoints (@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getAllPoints(user));
    }

    @PostMapping("/checkPoint")
    public ResponseEntity<PointResponseDTO> checkPoint (@Validated @RequestBody PointRequestDTO point, @AuthenticationPrincipal User user) {
        PointResponseDTO response = userService.addPoint(point, user);
        return ResponseEntity.ok(response);
    }
    
    
    @PostMapping("/removePoints")
    public ResponseEntity<String> removePoints (@AuthenticationPrincipal User user) {
        userService.removePoints(user);
        return ResponseEntity.ok("Success");
    }
    
}
