package com.andrew.dto.VisitRequest;

import java.time.LocalDate;
import java.util.List;

import com.andrew.dto.delivery.DeliveryResponseDTO;
import com.andrew.model.enums.VisitRequestStatus;

public record VisitRequestResponseDTO(
    Long id,
    Long userId,
    LocalDate date,
    VisitRequestStatus status,
    List<DeliveryResponseDTO> deliveries
) {}