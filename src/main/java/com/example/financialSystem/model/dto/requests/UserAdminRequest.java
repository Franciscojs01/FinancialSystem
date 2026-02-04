package com.example.financialSystem.model.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAdminRequest {
    @NotBlank String name;
    @NotBlank private String email;
    @NotBlank private String password;
    private LocalDate anniversaryDate;
}
