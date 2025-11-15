package com.example.financialSystem.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserRequest {
    private String name;
    private String email;
    private String password;

}


