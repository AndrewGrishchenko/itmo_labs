package com.andrew.lab4.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class SignInDTO {
    @NotNull (message = "Username cannot be null")
    @NotBlank (message = "Username cannot be empty")
    @Size (max = 15, message = "Username must be up to 15 characters")
    private String username;

    @NotNull (message = "Password must not be null")
    @NotBlank (message = "Password must not be empty")
    @Size (max = 25, message = "Password must be up to 25 characters")
    String password;
}
