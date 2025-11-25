package com.example.financialSystem.model.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    private int Id;
    private String userName;
    private String token;

}
