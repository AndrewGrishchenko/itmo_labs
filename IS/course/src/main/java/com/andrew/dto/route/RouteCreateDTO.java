package com.andrew.dto.route;

import jakarta.validation.constraints.NotNull;

public record RouteCreateDTO(
    @NotNull Long routeRequestId
) {}
