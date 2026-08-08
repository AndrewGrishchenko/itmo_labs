package com.andrew.dto.document;

import java.time.LocalDate;

import com.andrew.model.DocType;

public record DocumentResponseDTO(
    Long id,
    Long ownerId,
    DocType docType,
    LocalDate validUntil,
    String filePath
) {}
