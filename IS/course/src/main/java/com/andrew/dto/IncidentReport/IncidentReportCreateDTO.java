package com.andrew.dto.IncidentReport;

import com.andrew.model.enums.SupplyType;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record IncidentReportCreateDTO(
    @NotNull SupplyType supplyType,
    @NotNull @NotEmpty String description
) {}
