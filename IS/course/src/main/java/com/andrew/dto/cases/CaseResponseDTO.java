package com.andrew.dto.cases;

import com.andrew.model.enums.CaseStatus;

public record CaseResponseDTO(
    Long id,
    String description,
    CaseStatus status
) {}
