package com.andrew.service;

import java.sql.SQLException;
import java.util.Optional;

import com.andrew.dto.route.RouteBuildDTO;
import com.andrew.exceptions.NotFoundException;
import com.andrew.exceptions.RouteBuildException;
import com.andrew.model.Route;
import com.andrew.model.RouteRequest;
import com.andrew.model.enums.RouteRequestStatus;
import com.andrew.model.enums.RouteStatus;
import com.andrew.repository.RouteRepository;
import com.andrew.repository.RouteRequestRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ForbiddenException;

@ApplicationScoped
public class RouteManagementService {
    @Inject
    RouteRepository routeRepository;

    @Inject
    RouteRequestRepository routeRequestRepository;

    @Inject
    RequirementService requirementService;

    @Transactional
    public Route getRouteById(Long id) {
        return routeRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Route", id));
    }

    @Transactional
    public void buildRoute(Long id, RouteBuildDTO dto) {
        Route route = routeRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Route", id));

        if (!route.getStatus().equals(RouteStatus.DRAFT))
            throw new ForbiddenException();

        try {
            routeRepository.buildRoute(id, dto.zoneIds());
        } catch (Exception ex) {
            String msg = extractSqlErrorMessage(ex);
            throw new RouteBuildException("Failed to build route: " + msg);
        }
    }

    private String extractSqlErrorMessage(Throwable t) {
        if (t == null) return "Unknown error";

        if (t instanceof org.hibernate.exception.GenericJDBCException gjex) {
            SQLException sqlEx = gjex.getSQLException();
            if (sqlEx != null) {
                return getPostgresMessage(sqlEx);
            }
        } else if (t instanceof java.sql.SQLException sqle) {
            return getPostgresMessage(sqle);
        }

        return extractSqlErrorMessage(t.getCause());
    }

    private String getPostgresMessage(SQLException sqlEx) {
        StringBuilder sb = new StringBuilder();
        while (sqlEx != null) {
            String msg = sqlEx.getMessage();
            if (msg != null && !msg.isEmpty()) {
                String[] lines = msg.split("\n");
                for (String line : lines) {
                    line = line.trim();
                    if (line.startsWith("ОШИБКА:")) {
                        sb.append(line.substring("ОШИБКА:".length()).trim());
                    }
                }
            }
            sqlEx = sqlEx.getNextException();
        }
        return sb.toString().trim();
    }

    @Transactional
    public void approveRoute(Long id) {
        Route route = getRouteById(id);

        if (!route.getStatus().equals(RouteStatus.DRAFT))
            throw new ForbiddenException();

        route.setStatus(RouteStatus.APPROVED);
        route.getRequest().setStatus(RouteRequestStatus.APPROVED);
        
        routeRepository.update(route);
    }

    @Transactional
    public void rejectRouteByKeeper(Long id) {
        RouteRequest routeRequest = getRouteRequestById(id);

        if (!routeRequest.getStatus().equals(RouteRequestStatus.SUBMITTED))
            throw new ForbiddenException();

        routeRequest.setStatus(RouteRequestStatus.REJECTED);
        routeRequestRepository.update(routeRequest);

        Optional<Route> route = routeRepository.findByRequestId(id);
        if (route.isPresent()) {
            route.get().setStatus(RouteStatus.REJECTED);
            routeRepository.update(route.get());
        }
    }

    @Transactional
    public void startRoute(Long id) {
        Route route = getRouteById(id);

        if (!route.getStatus().equals(RouteStatus.APPROVED))
            throw new ForbiddenException();

        route.setStatus(RouteStatus.IN_PROGRESS);

        routeRepository.update(route);
    }

    @Transactional
    public void completeRoute(Long id) {
        Route route = getRouteById(id);

        if (!route.getStatus().equals(RouteStatus.IN_PROGRESS))
            throw new ForbiddenException();

        route.setStatus(RouteStatus.COMPLETED);
        // route.setCurrentDate();

        routeRepository.update(route);

        requirementService.calculateFulfillment();
    }

    @Transactional
    public RouteRequest getRouteRequestById(Long id) {
        return routeRequestRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("RouteRequest", id));
    }

    @Transactional
    public void submitRouteRequest(Long id) {
        RouteRequest routeRequest = getRouteRequestById(id);
        
        if (!routeRequest.getStatus().equals(RouteRequestStatus.DRAFT))
            throw new ForbiddenException();

        routeRequest.setStatus(RouteRequestStatus.SUBMITTED);
        routeRequestRepository.update(routeRequest);
    }

    @Transactional
    public void rejectRouteRequestByCaptain(Long id) {
        RouteRequest routeRequest = getRouteRequestById(id);
        Route route = routeRepository.findByRequestId(id)
            .orElseThrow(() -> new ForbiddenException());

        if (!routeRequest.getStatus().equals(RouteRequestStatus.APPROVED))
            throw new ForbiddenException();

        routeRequest.setStatus(RouteRequestStatus.REJECTED);
        route.setStatus(RouteStatus.REJECTED);

        routeRequestRepository.update(routeRequest);
        routeRepository.update(route);
    }

    @Transactional
    public void assertCanBeRequestComplained(Long routeRequestId) {
        RouteRequest routeRequest = getRouteRequestById(routeRequestId);

        if (!routeRequest.getStatus().equals(RouteRequestStatus.REJECTED))
            throw new ForbiddenException();
    }
}
