package com.example.financialSystem.util;

import com.example.financialSystem.model.dto.requests.UserRequest;

public class UserPutRequestBodyCreator {
    public static UserRequest updateUserPutRequestBody() {
        return UserRequest.builder()
                .name(UserCreator.updateUser().getName())
                .email(UserCreator.updateUser().getEmail())
                .build();
    }
}
