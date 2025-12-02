package com.example.financialSystem.model.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserPatchRequest {
    private String name;
    private String email;
    private String password;
}
