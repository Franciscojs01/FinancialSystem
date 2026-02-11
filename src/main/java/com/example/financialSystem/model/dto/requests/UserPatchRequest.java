package com.example.financialSystem.model.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPatchRequest {
    @Schema(description = "Name of the user", example = "John Doe")
    private String name;
    @Schema(description = "Email of the user", example = "default@gmail.com")
    private String email;
    private String password;
}
