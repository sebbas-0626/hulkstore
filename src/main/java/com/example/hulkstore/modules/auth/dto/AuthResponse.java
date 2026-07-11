package com.example.hulkstore.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
public class AuthResponse {
    // 1. Agregamos el campo para el mensaje personalizado
    private String message;

    @NotBlank(message = "Access token is required")
    private String accessToken;

    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}