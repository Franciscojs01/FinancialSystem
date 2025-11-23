package com.example.financialSystem.dto.requests;

import jakarta.persistence.Column;

public record LoginRequest(
        @Column(unique = true, nullable = false)
        String username,

        @Column(nullable = false)
        String password
) {
}
