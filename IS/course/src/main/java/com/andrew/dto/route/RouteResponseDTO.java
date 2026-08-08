package com.andrew.dto.route;

import java.util.List;

import com.andrew.model.enums.RouteStatus;

public record RouteResponseDTO(
    Long id,
    Long routeRequestId,
    RouteStatus status,
    List<Long> zoneIds
) {}
