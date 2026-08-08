package com.andrew.dto.segment;

public record SegmentResponseDTO(
    Long id,
    Long routeId,
    Long zoneId,
    Long stepIndex
) {}
