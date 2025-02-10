package com.andrew.lab4.services;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.andrew.lab4.dto.PointRequestDTO;
import com.andrew.lab4.dto.PointResponseDTO;

@Service
public class AreaService {
    public PointResponseDTO checkPoint(PointRequestDTO request) {
        PointResponseDTO response = PointResponseDTO.builder()
            .x(request.getX())
            .y(request.getY())
            .r(request.getR())
            .curTime(LocalDateTime.now().toString())
            .build();
        
        long startTime = System.nanoTime();
        response.setHit(isHit(request.getX(), request.getY(), request.getR()));
        response.setExecTime((System.nanoTime() - startTime) / 1000);
        return response;
    }

    private boolean isHit (double x, double y, double r) {
        if (r == 0) return false;
        return ((-r/2 <= x && x <= 0) && (0 <= y && y <= -2 * x + r))
            || ((x >= 0 && y >= 0) && (x <= r && y <= r))
            || ((x >= 0 && y <= 0) && (x*x + y*y <= r*r));
    }
}
