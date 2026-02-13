package com.example.financialSystem.util;

import com.example.financialSystem.models.dto.requests.UserRequest;

import java.time.LocalDate;

public class UserPutRequestBodyCreator {
    public static UserRequest updateUserPutRequestBody() {
        return UserRequest.builder()
                .name(UserCreator.updateUser().getName())
                .email(UserCreator.updateUser().getEmail())
                .password("123")
                .anniversaryDate(LocalDate.of(2008, 1, 1))
                .build();
    }
}