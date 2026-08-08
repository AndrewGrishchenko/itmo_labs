package com.andrew.dto.complaint;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ComplaintCreateDTO(
    @NotNull Long routeRequestId,
    @NotNull @NotEmpty String description
) {}
