package com.example.financialSystem.model.dto.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserRequest {
    @NotNull(message = "name is required")
    private String name;

    @NotNull(message = "email is required")
    private String email;

    @NotNull(message = "password is required")
    private String password;

}


