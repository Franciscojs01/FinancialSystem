package com.example.financialSystem.models.dto.responses;

public record RefreshTokenResponse(
            String refreshToken,
            String tokenType,
            String accessToken
) {
}
