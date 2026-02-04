package com.example.financialSystem.controller;

import com.example.financialSystem.model.dto.requests.UserAdminRequest;
import com.example.financialSystem.model.dto.requests.UserRequest;
import com.example.financialSystem.model.dto.responses.UserResponse;
import com.example.financialSystem.model.entity.User;
import com.example.financialSystem.service.UserService;
import com.example.financialSystem.util.UserCreator;
import com.example.financialSystem.util.UserPatchRequestBodyCreator;
import com.example.financialSystem.util.UserPostRequestBodyCreator;
import com.example.financialSystem.util.UserPutRequestBodyCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
class UserControllerTest {
    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userServiceMock;

    @BeforeEach
    void setUp() {
        User user = UserCreator.createUser();
        UserResponse response = new UserResponse(user.getName(), user.getEmail());

        User adminUser = UserCreator.createUserAdmin();
        UserResponse adminResponse = new UserResponse(adminUser.getName(), adminUser.getEmail());

        UserResponse response2 = new UserResponse(
                "Jane Doe",
                "jose@gmail.com"
        );

        UserResponse response3 = new UserResponse(
                "John Smith",
                "john@gmail.com"
        );

        List<UserResponse> responseList = List.of(UserCreator.createValidUser());

        BDDMockito.when(userServiceMock.registerUser(ArgumentMatchers.any(UserRequest.class)))
                .thenReturn(response);

        BDDMockito.when(userServiceMock.registerAdminUser(ArgumentMatchers.any(UserAdminRequest.class)))
                .thenReturn(adminResponse);

        BDDMockito.when(userServiceMock.userUpdate(ArgumentMatchers.anyInt(), ArgumentMatchers.any(UserRequest.class)))
                .thenReturn(response2);

        BDDMockito.when(userServiceMock.userPatch(ArgumentMatchers.anyInt(), ArgumentMatchers.any()))
                .thenReturn(response3);

        BDDMockito.when(userServiceMock.listAllUser())
                .thenReturn(responseList);

        BDDMockito.when(userServiceMock.getUserById(ArgumentMatchers.anyInt()))
                .thenReturn(response);

        BDDMockito.doNothing().when(userServiceMock).deactivateUser(ArgumentMatchers.anyInt());

        BDDMockito.doNothing().when(userServiceMock).activateUser(ArgumentMatchers.anyInt());
    }

    @Test
    @DisplayName("Return User When create User is Successful")
    void register_DefaultUser_WhenSuccessful() {
        UserResponse userResponse = userController.registerUser(UserPostRequestBodyCreator.createUserPostRequestBody())
                .getBody();

        Assertions.assertThat(userResponse).isNotNull();

        Assertions.assertThat(userResponse).isEqualTo(UserCreator.createValidUser());
    }

    @Test
    @DisplayName("Return User When create User is Successful")
    void register_AdminUser_WhenSuccessful() {
        UserResponse userResponse =
                userController.registerAdminUser(UserPostRequestBodyCreator.createAdminUserPostRequestBody())
                        .getBody();

        Assertions.assertThat(userResponse).isNotNull();

        Assertions.assertThat(userResponse).isEqualTo(UserCreator.createValidAdminUser());

    }

    @Test
    @DisplayName("Return User When update User is Successful")
    void update_User_WhenSuccessful() {

        ResponseEntity<UserResponse> responseEntity = userController.editUser
                (UserCreator.updateUser().getId(), UserPutRequestBodyCreator.updateUserPutRequestBody());

        Assertions.assertThat(responseEntity).isNotNull();

        UserResponse userResponse = responseEntity.getBody();
        Assertions.assertThat(userResponse).isNotNull();

        Assertions.assertThat(userResponse.getName()).isEqualTo("Jane Doe");
        Assertions.assertThat(userResponse.getEmail()).isEqualTo("jose@gmail.com");

    }

    @Test
    @DisplayName("Return User When patch User is Successful")
    void patch_User_WhenSuccessful() {

        ResponseEntity<UserResponse> responseEntity = userController.patchUser(
                (UserCreator.patchUser().getId()), UserPatchRequestBodyCreator.patchUserRequestBody());

        Assertions.assertThat(responseEntity).isNotNull();

        UserResponse userResponse = responseEntity.getBody();
        Assertions.assertThat(userResponse).isNotNull();

        Assertions.assertThat(userResponse.getName()).isEqualTo("John Smith");
        Assertions.assertThat(userResponse.getEmail()).isEqualTo("john@gmail.com");

    }

    @Test
    @DisplayName("Return List of Users When listAll Users is Successful")
    void listAll_User_WhenSucessful() {
        ResponseEntity<List<UserResponse>> responseEntity = userController.getAllUsers();

        Assertions.assertThat(responseEntity).isNotNull();

        List<UserResponse> userResponseList = responseEntity.getBody();
        Assertions.assertThat(userResponseList).isNotNull();
        Assertions.assertThat(userResponseList).isNotEmpty();
        Assertions.assertThat(userResponseList.get(0)).isEqualTo(UserCreator.createValidUser());
    }

    @Test
    @DisplayName("Return User When getUserById is Successful")
    void getUserById_WhenSuccessful() {
        ResponseEntity<UserResponse> responseEntity = userController.getUser(1);

        Assertions.assertThat(responseEntity).isNotNull();

        UserResponse userResponse = responseEntity.getBody();
        Assertions.assertThat(userResponse).isNotNull();
        Assertions.assertThat(userResponse).isEqualTo(UserCreator.createValidUser());
    }

    @Test
    @DisplayName("Verify deactivateUser is Successful")
    void deactivateUser_WhenSuccessful() {
        ResponseEntity<Void> responseEntity = userController.deactivateUser(1);
        Assertions.assertThat(responseEntity).isNotNull();
        Assertions.assertThat(responseEntity.getStatusCodeValue()).isEqualTo(204);
    }

    @Test
    @DisplayName("Verify activateUser is Successful")
    void activateUser_WhenSuccessful() {
        ResponseEntity<Void> responseEntity = userController.activateUser(1);
        Assertions.assertThat(responseEntity).isNotNull();
        Assertions.assertThat(responseEntity.getStatusCodeValue()).isEqualTo(204);
    }
}