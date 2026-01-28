package com.example.financialSystem.util;

import com.example.financialSystem.model.dto.requests.UserRequest;

import java.time.LocalDate;

public class UserPostRequestBodyCreator {
    public static UserRequest createUserPostRequestBody() {
        return UserRequest.builder()
                .name("chico")
                .email("chiquin@.com")
                .password("1234")
                .anniversaryDate(LocalDate.of(2008, 1, 1))
                .build();

    }
}