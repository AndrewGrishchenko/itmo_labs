package com.andrew.dto.RouteRequest;

import java.util.List;

import com.andrew.model.Goal;
import com.andrew.model.ShipType;
import com.andrew.model.enums.RouteRequestStatus;

public record RouteRequestResponseDTO(
    Long id,
    Long shipId,
    ShipType shipType,
    Long sourceZoneId,
    Long targetZoneId,
    Goal goal,
    List<Long> documentsIds,
    RouteRequestStatus status
) {}
