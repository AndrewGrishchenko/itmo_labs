package com.andrew.lab4.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PointRequestDTO {
    @NotNull(message = "x cannot be null")
    private double x;

    @NotNull(message = "y cannot be null")
    private double y;

    @NotNull(message = "r cannot be null")
    @DecimalMin(value = "0", message = "r must be greater than or equal to 0")
    private double r;
}
