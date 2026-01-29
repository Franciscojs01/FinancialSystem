package com.example.financialSystem.controller;

import com.example.financialSystem.model.dto.requests.UserRequest;
import com.example.financialSystem.model.dto.responses.UserResponse;
import com.example.financialSystem.model.entity.User;
import com.example.financialSystem.service.UserService;
import com.example.financialSystem.util.UserCreator;
import com.example.financialSystem.util.UserPostRequestBodyCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class UserControllerTest {
    @InjectMocks
    private UserController userController;
    @Mock
    private UserService userServiceMock;

    @BeforeEach
    void setUp() {
        User user = UserCreator.createUser();
        UserResponse response = new UserResponse(user.getName(), user.getEmail(), user.getAnniversaryDate());

        BDDMockito.when(userServiceMock.registerUser(ArgumentMatchers.any(UserRequest.class)))
                .thenReturn(response);

        BDDMockito.when(userServiceMock.userUpdate(user.getId(), ArgumentMatchers.any(UserRequest.class)))
                .thenReturn(response);

    }

    @Test
    @DisplayName("Return User When create User is Successful")
    void register_DefaultUser_WhenSuccessful () {
        UserResponse userResponse = userController.registerUser(UserPostRequestBodyCreator.createUserPostRequestBody()).getBody();

        Assertions.assertThat(userResponse).isNotNull();

        Assertions.assertThat(userResponse).isEqualTo(UserCreator.createValidUser());
    }





}