package com.andrew.dto.supply;

public record SupplyResponseDTO(
    Long id,
    String name,
    Integer amount
) {}