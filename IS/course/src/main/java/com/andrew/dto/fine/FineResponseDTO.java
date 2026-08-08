package com.andrew.dto.fine;

import java.math.BigDecimal;

public record FineResponseDTO(
    Long id,
    Long caseId,
    BigDecimal amount
) {}
