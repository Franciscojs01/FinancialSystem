package com.example.financialSystem.util;

import com.example.financialSystem.model.dto.requests.UserAdminRequest;
import com.example.financialSystem.model.dto.requests.UserRequest;

import java.time.LocalDate;

public class UserPostRequestBodyCreator {
    public static UserRequest createUserPostRequestBody() {
        return UserRequest.builder()
                .name("New User")
                .email("newuser@gmail.com")
                .password("123")
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

    public static UserAdminRequest createAdminUserPostITRequestBody() {
        return UserAdminRequest.builder()
                .name("Integration Admin")
                .email("integration-admin")
                .password("admin321")
                .anniversaryDate(LocalDate.of(2008, 1, 1))
                .build();
    }

    public static UserRequest createUserPostITRequestBody() {
        return UserRequest.builder()
                .name("Integration User")
                .email("integration-user")
                .password("user321")
                .anniversaryDate(LocalDate.of(2008, 1, 1))
                .build();
    }
}