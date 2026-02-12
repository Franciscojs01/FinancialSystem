package com.example.financialSystem.util;

import com.example.financialSystem.models.dto.requests.UserPatchRequest;

public class UserPatchRequestBodyCreator {
    public static UserPatchRequest patchUserRequestBody() {
        return UserPatchRequest.builder()
                .name(UserCreator.patchUser().getName())
                .build();
    }
}