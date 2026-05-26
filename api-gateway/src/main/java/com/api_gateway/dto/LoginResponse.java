package com.api_gateway.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long expiresIn,
        String email,
        String role
) {}
