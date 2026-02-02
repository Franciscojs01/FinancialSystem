package com.example.financialSystem.model.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserAdminRequest {
    @NotBlank String name;
    @NotBlank private String email;
    @NotBlank private String password;
    private LocalDate anniversaryDate;
}
