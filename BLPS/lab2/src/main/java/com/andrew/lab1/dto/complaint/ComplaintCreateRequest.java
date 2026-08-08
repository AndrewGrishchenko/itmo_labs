package com.andrew.lab1.dto.complaint;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ComplaintCreateRequest(
    @NotNull(message = "Video ID is a must")
    Long videoId,

    @NotBlank(message = "Claim detail must not be blank")
    String claimDetails
) {}
