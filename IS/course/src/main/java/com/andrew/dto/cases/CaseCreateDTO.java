package com.andrew.dto.cases;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CaseCreateDTO(
    @NotNull @NotEmpty String description
) {}
