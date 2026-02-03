package com.example.financialSystem.util;

import com.example.financialSystem.model.dto.requests.UserAdminRequest;
import com.example.financialSystem.model.dto.requests.UserRequest;

import java.time.LocalDate;

public class UserPostRequestBodyCreator {
    public static UserRequest createUserPostRequestBody() {
        return UserRequest.builder()
                .name(UserCreator.createUser().getName())
                .email(UserCreator.createUser().getEmail())
                .password(UserCreator.createUser().getLogin().getPassword())
                .anniversaryDate(LocalDate.of(2008, 1, 1))
                .build();
    }

    public static UserAdminRequest createAdminUserPostRequestBody() {
        return UserAdminRequest.builder()
                .name(UserCreator.createUserAdmin().getName())
                .email(UserCreator.createUserAdmin().getEmail())
                .password(UserCreator.createUserAdmin().getLogin().getPassword())
                .anniversaryDate(LocalDate.of(2008, 1, 1))
                .build();
    }
}