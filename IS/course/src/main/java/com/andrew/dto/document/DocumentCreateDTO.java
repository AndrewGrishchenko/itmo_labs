package com.andrew.dto.document;

import java.time.LocalDate;

import com.andrew.model.DocType;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record DocumentCreateDTO(
    @NotNull Long ownerId,
    @NotNull DocType docType,
    @NotNull LocalDate validUntil,
    @NotNull @NotEmpty String filePath
) {}
