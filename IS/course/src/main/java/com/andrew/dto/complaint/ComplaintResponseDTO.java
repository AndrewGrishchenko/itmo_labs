package com.andrew.dto.complaint;

import java.math.BigDecimal;

import com.andrew.model.enums.CaseStatus;

public record ComplaintResponseDTO(
    Long id,
    Long caseId,
    CaseStatus status,
    String description,
    Long routeRequestId,
    BigDecimal fineAmount
) {}
