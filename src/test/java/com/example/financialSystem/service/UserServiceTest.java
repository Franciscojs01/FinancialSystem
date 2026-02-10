package com.example.financialSystem.service;

import com.example.financialSystem.model.dto.requests.UserPatchRequest;
import com.example.financialSystem.model.dto.requests.UserRequest;
import com.example.financialSystem.model.dto.responses.UserResponse;
import com.example.financialSystem.model.entity.Login;
import com.example.financialSystem.model.entity.User;
import com.example.financialSystem.model.mapper.UserMapper;
import com.example.financialSystem.repository.LoginRepository;
import com.example.financialSystem.repository.UserRepository;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private LoginRepository loginRepositoryMock;

    @Mock
    private UserMapper userMapperMock;

    @Mock
    private PasswordEncoder passwordEncoderMock;

    @BeforeEach
    void setUp() {
        BDDMockito.when(userRepositoryMock.save(ArgumentMatchers.any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BDDMockito.when(passwordEncoderMock.encode(ArgumentMatchers.anyString()))
                .thenReturn("encoded");

        List<UserResponse> expectedUserResponses = List.of(UserCreator.createValidUser());
        BDDMockito.when(userMapperMock.toResponseList(ArgumentMatchers.anyList()))
                .thenReturn(expectedUserResponses);

        BDDMockito.when(userRepositoryMock.findById(1))
                .thenReturn(Optional.of(UserCreator.createUser()));
        BDDMockito.when(userRepositoryMock.findById(2))
                .thenReturn(Optional.of(UserCreator.createInactiveUser()));
        BDDMockito.when(userRepositoryMock.findById(3))
                .thenReturn(Optional.of(UserCreator.createActiveUser()));

        User existingForUpdate = UserCreator.createUser();
        existingForUpdate.setId(4);
        existingForUpdate.setName("Old Name");
        existingForUpdate.setEmail("old@gmail.com");
        existingForUpdate.getLogin().setUsername(existingForUpdate.getEmail());

        User existingForPatch = UserCreator.createUser();
        existingForPatch.setId(5);
        existingForPatch.setName("Old Name");
        existingForPatch.setEmail("old@gmail.com");
        existingForPatch.getLogin().setUsername(existingForPatch.getEmail());

        BDDMockito.when(userRepositoryMock.findById(4))
                .thenReturn(Optional.of(existingForUpdate));
        BDDMockito.when(userRepositoryMock.findById(5))
                .thenReturn(Optional.of(existingForPatch));

        BDDMockito.when(loginRepositoryMock.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        Login logged = new Login();
        User admin = UserCreator.createUserAdmin();
        logged.setUser(admin);
        logged.setUsername("logged@gmail.com");
        logged.setPassword("encoded");

        BDDMockito.when(loginRepositoryMock.findByUsername(ArgumentMatchers.eq("logged@gmail.com")))
                .thenReturn(Optional.of(logged));

        BDDMockito.when(userMapperMock.toEntity(ArgumentMatchers.any()))
                .thenAnswer(inv -> {
                    var req = (UserRequest) inv.getArgument(0);
                    User u = new User();
                    u.setName(req.getName());
                    u.setEmail(req.getEmail());
                    u.setDeleted(Boolean.FALSE);
                    return u;
                });

        BDDMockito.when(userMapperMock.toEntityAdmin(ArgumentMatchers.any()))
                .thenAnswer(inv -> {
                    var req = (UserRequest) inv.getArgument(0);
                    User u = new User();
                    u.setName(req.getName());
                    u.setEmail(req.getEmail());
                    u.setDeleted(Boolean.FALSE);
                    return u;
                });

        BDDMockito.when(userMapperMock.toRequest(ArgumentMatchers.any(User.class)))
                .thenAnswer(inv -> {
                    User u = inv.getArgument(0);
                    return UserRequest.builder()
                            .name(u.getName())
                            .email(u.getEmail())
                            .build();
                });

        BDDMockito.doAnswer(inv -> {
            var req = (UserRequest) inv.getArgument(0);
            User u = inv.getArgument(1);
            u.setName(req.getName());
            u.setEmail(req.getEmail());
            if (u.getLogin() != null) u.getLogin().setUsername(req.getEmail());
            return null;
        }).when(userMapperMock).updateEntityFromUpdate(
                ArgumentMatchers.any(), ArgumentMatchers.any(User.class)
        );

        BDDMockito.doAnswer(inv -> {
            var req = (UserPatchRequest) inv.getArgument(0);
            User u = inv.getArgument(1);
            if (req.getName() != null) u.setName(req.getName());
            if (req.getEmail() != null) {
                u.setEmail(req.getEmail());
                if (u.getLogin() != null) u.getLogin().setUsername(req.getEmail());
            }
            return null;
        }).when(userMapperMock).updateEntityFromPatch(
                ArgumentMatchers.any(), ArgumentMatchers.any(User.class)
        );

        BDDMockito.when(userMapperMock.toResponse(ArgumentMatchers.any(User.class)))
                .thenAnswer(inv -> {
                    User u = inv.getArgument(0);
                    return new UserResponse(u.getName(), u.getEmail());
                });

        SecurityContext securityContext = BDDMockito.mock(SecurityContext.class);
        Authentication authentication = BDDMockito.mock(Authentication.class);

        BDDMockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        BDDMockito.when(authentication.getName()).thenReturn("logged@gmail.com");

        SecurityContextHolder.setContext(securityContext);
    }


    @Test
    @DisplayName("Should return UserResponse when registering a default user successfully")
    void registerUser_ReturnsUserResponse_WhenSuccessful() {
        UserRequest request = UserPostRequestBodyCreator.createUserPostRequestBody();
        UserResponse userResponse = userService.registerUser(request);

        Assertions.assertThat(userResponse).isNotNull();
        Assertions.assertThat(userResponse.getName()).isEqualTo(request.getName());
        Assertions.assertThat(userResponse.getEmail()).isEqualTo(request.getEmail());
    }


    @Test
    @DisplayName("Return User When create Admin User is Successful")
    void register_AdminUser_WhenSuccessful() {
        UserResponse userResponse =
                userService.registerAdminUser(UserPostRequestBodyCreator.createAdminUserPostRequestBody());

        Assertions.assertThat(userResponse).isNotNull();
        Assertions.assertThat(userResponse).isEqualTo(UserCreator.createValidAdminUser());
    }

    @Test
    @DisplayName("Should return updated UserResponse when updating a user successfully")
    void updateUser_ReturnsUpdatedUserResponse_WhenSuccessful() {
        var req = UserPutRequestBodyCreator.updateUserPutRequestBody();

        UserResponse userResponse = userService.updateUser(4, req);

        Assertions.assertThat(userResponse).isNotNull();
        Assertions.assertThat(userResponse.getName()).isEqualTo(req.getName());
        Assertions.assertThat(userResponse.getEmail()).isEqualTo(req.getEmail());
    }


    @Test
    @DisplayName("Return User When patch User is Successful")
    void patch_User_WhenSuccessful() {
        UserResponse userResponse = userService.patchUser(
                UserCreator.patchUser().getId(), UserPatchRequestBodyCreator.patchUserRequestBody());

        Assertions.assertThat(userResponse).isNotNull();
        Assertions.assertThat(userResponse.getName()).isEqualTo(UserCreator.patchUser().getName());
    }

    @Test
    @DisplayName("Return List of Users When listAll Users is Successful")
    void listAll_User_WhenSuccessful() {
        List<UserResponse> userResponseList = userService.listAllUser();

        Assertions.assertThat(userResponseList).isNotNull();
        Assertions.assertThat(userResponseList).isNotEmpty();
        Assertions.assertThat(userResponseList).hasSize(1);
        Assertions.assertThat(userResponseList.get(0)).isEqualTo(UserCreator.createValidUser());
    }

    @Test
    @DisplayName("Return User When getUserById is Successful")
    void getUserById_WhenSuccessful() {
        UserResponse userResponse = userService.getUserById(1);

        Assertions.assertThat(userResponse).isNotNull();
        Assertions.assertThat(userResponse).isEqualTo(UserCreator.createValidUser());
    }

    @Test
    @DisplayName("Verify deactivateUser is Successful")
    void deactivateUser_WhenSuccessful() {
        Assertions.assertThatCode(() -> userService.deactivateUser(3))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Verify activateUser is Successful")
    void activateUser_WhenSuccessful() {
        Assertions.assertThatCode(() -> userService.activateUser(2))
                .doesNotThrowAnyException();
    }

}