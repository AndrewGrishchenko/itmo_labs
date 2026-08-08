package com.andrew.service;

import java.util.List;

import com.andrew.dto.route.RouteCreateDTO;
import com.andrew.dto.route.RouteResponseDTO;
import com.andrew.exceptions.NotFoundException;
import com.andrew.mapper.dto.RouteMapper;
import com.andrew.model.Route;
import com.andrew.model.enums.RouteRequestStatus;
import com.andrew.repository.RouteRepository;
import com.andrew.security.CurrentUser;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ForbiddenException;

@ApplicationScoped
public class RouteService {
    @Inject
    RouteRepository routeRepository;

    @Inject
    RouteMapper routeMapper;

    @Inject
    RouteRequestService routeRequestService;

    @Inject
    CurrentUser currentUser;

    @Transactional
    public List<RouteResponseDTO> getAll() {
        List<Route> routes = switch (currentUser.getUser().getRole()) {
            case CAPTAIN -> routeRepository.getByCaptainId(currentUser.getUser().getId());
            case KEEPER, BOSS, ADMIN -> routeRepository.getAll();
            default -> throw new ForbiddenException();
        };

        return routes.stream().map(routeMapper::toResponse).toList();
    }

    @Transactional
    public RouteResponseDTO createRoute(RouteCreateDTO dto) {
        if (routeRepository.existsByRequestId(dto.routeRequestId()))
            throw new ForbiddenException();

        routeRequestService.assertStatus(dto.routeRequestId(), RouteRequestStatus.SUBMITTED);
        
        return createRoute(routeMapper.toEntity(dto));
    }

    @Transactional
    private RouteResponseDTO createRoute(Route route) {
        routeRepository.save(route);
        return routeMapper.toResponse(route);
    }

    @Transactional
    public RouteResponseDTO updateRoute(Long id, RouteCreateDTO dto) {
        routeRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Route", id));

        Route toUpdate = routeMapper.toEntity(dto);
        toUpdate.setId(id);
        
        return routeMapper.toResponse(routeRepository.update(toUpdate));
    }

    @Transactional
    public void deleteRoute(Long id) {
        Route route = routeRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Route", id));
        
        routeRepository.delete(route);
    }
}
