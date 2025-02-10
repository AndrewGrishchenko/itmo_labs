package com.andrew.lab4.services;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.andrew.lab4.dto.PointRequestDTO;
import com.andrew.lab4.dto.PointResponseDTO;
import com.andrew.lab4.model.Point;
import com.andrew.lab4.model.User;
import com.andrew.lab4.repos.PointRepository;
import com.andrew.lab4.repos.UserRepository;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PointRepository pointRepository;
    private final AreaService areaService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService (UserRepository userRepository, PointRepository pointRepository, AreaService areaService) {
        this.userRepository = userRepository;
        this.pointRepository = pointRepository;
        this.areaService = areaService;
    }

    public User register(String username, String email, String password) {
        String hashedPassword = passwordEncoder.encode(password);
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public Point savePoint (Point result) {
        return pointRepository.save(result);
    }

    public List<Point> getPointsForUser (User user) {
        return pointRepository.findByUser(user);
    }

    public List<PointResponseDTO> getAllPoints (User user) {
        return pointRepository.findByUser(user)
            .stream()
            .map(PointResponseDTO::new)
            .toList();
    }

    public PointResponseDTO addPoint (PointRequestDTO request, User user) {
        PointResponseDTO response = areaService.checkPoint(request);
        Point point = response.toPoint();
        point.setUser(user);
        savePoint(point);
        return response;
    }

    public void removePoints (User user) {
        pointRepository.deleteAllByUserId(user.getId());
    }
}
