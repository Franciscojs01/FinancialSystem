package com.example.financialSystem.models.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public record LoginResponse(
        UUID id,
        String username,
        String accessToken,
        String refreshToken
) {}