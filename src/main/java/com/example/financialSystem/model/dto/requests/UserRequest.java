package com.example.financialSystem.model.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserRequest {
    @NotBlank(message = "name cannot be blank")
    private String name;
    @NotBlank(message = "email cannot be blank")
    private String email;
    @NotBlank(message = "password cannot be blank")
    private String password;
    @NotNull(message = "anniversaryDate cannot be null")
    private LocalDate anniversaryDate;

}
