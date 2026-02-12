package com.example.financialSystem.models.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Name of the user", example = "John Doe")
    private String name;

    @NotBlank(message = "email cannot be blank")
    @Schema(description = "Email of the user", example = "default@gmail.com")
    private String email;

    @NotBlank(message = "password cannot be blank")
    private String password;
    @NotNull(message = "anniversaryDate cannot be null")
    private LocalDate anniversaryDate;

}
