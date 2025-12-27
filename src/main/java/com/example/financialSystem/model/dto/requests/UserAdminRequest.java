package com.example.financialSystem.model.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserAdminRequest {
    @NotBlank String name;
    @NotBlank private String email;
    @NotBlank private String password;
}
