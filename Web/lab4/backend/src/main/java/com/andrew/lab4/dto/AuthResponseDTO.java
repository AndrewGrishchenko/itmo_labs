package com.andrew.lab4.dto;

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
public class AuthResponseDTO {
    @NotNull
    private String status;
    
    private String message;
    private String accessToken;
    private String refreshToken;

    @NotNull
    private String username;

    @NotNull
    private String email;
}
