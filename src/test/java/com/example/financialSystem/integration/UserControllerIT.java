package com.example.financialSystem.integration;

import com.example.financialSystem.models.dto.requests.LoginRequest;
import com.example.financialSystem.models.dto.requests.UserPatchRequest;
import com.example.financialSystem.models.dto.requests.UserRequest;
import com.example.financialSystem.models.dto.responses.LoginResponse;
import com.example.financialSystem.models.dto.responses.UserResponse;
import com.example.financialSystem.models.entity.Login;
import com.example.financialSystem.models.entity.User;
import com.example.financialSystem.models.enums.UserRole;
import com.example.financialSystem.repositories.LoginRepository;
import com.example.financialSystem.repositories.UserRepository;
import com.example.financialSystem.util.UserCreator;
import com.example.financialSystem.util.UserPatchRequestBodyCreator;
import com.example.financialSystem.util.UserPostRequestBodyCreator;
import com.example.financialSystem.util.UserPutRequestBodyCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
public class UserControllerIT {
    @Autowired
    private TestRestTemplate testRestTemplate;

    private static final String ADMIN_USERNAME = UserCreator.createUserAdmin().getEmail();
    private static final String ADMIN_PASSWORD = UserCreator.createUserAdmin().getLogin().getPassword();
    private static final String USER_USERNAME = UserCreator.createUser().getEmail();
    private static final String USER_PASSWORD = UserCreator.createUser().getLogin().getPassword();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private int adminId;
    private int userId;

    @BeforeEach
    void setUp() {
        loginRepository.deleteAll();
        userRepository.deleteAll();

        User savedAdmin = seedAdminUser();
        this.adminId = savedAdmin.getId();

        User savedUser = seedUser();
        this.userId = savedUser.getId();

        assert savedAdmin.getUserRole().equals(UserRole.ADMIN);
        assert savedUser.getUserRole().equals(UserRole.USER);
    }

    @Test
    @DisplayName("Register User When is Valid Return User")
    void registerUser_WhenValid_ReturnUser() {
        UserRequest userRequest = UserPostRequestBodyCreator.createUserPostITRequestBody();

        ResponseEntity<UserResponse> response =
                testRestTemplate.postForEntity("/user/register", userRequest, UserResponse.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("Register AdminUser When is Valid Return User")
    void registerAdminUser_WhenValid_ReturnUser() {
        UserRequest userRequest = UserPostRequestBodyCreator.createAdminUserPostITRequestBody();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(loginAsAdmin());
        HttpEntity<UserRequest> entity = new HttpEntity<>(userRequest, headers);

        ResponseEntity<UserResponse> response = testRestTemplate.exchange(
                "/user/admin/create",
                HttpMethod.POST,
                entity,
                UserResponse.class
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getName()).isEqualTo(userRequest.getName());
        Assertions.assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("Edit User When is Valid Return User")
    void editUser_WhenValid_ReturnUser() {
        var req = UserPutRequestBodyCreator.updateUserPutRequestBody();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(loginAsUser());
        HttpEntity<UserRequest> entity = new HttpEntity<>(req, headers);

        ResponseEntity<UserResponse> response = testRestTemplate.exchange(
                "/user/edit/" + userId,
                HttpMethod.PUT,
                entity,
                UserResponse.class
        );

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Patch User When is Valid Return User")
    void patchUser_WhenValid_ReturnUser() {
        var req = UserPatchRequestBodyCreator.patchUserRequestBody();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(loginAsUser());
        HttpEntity<UserPatchRequest> entity = new HttpEntity<>(req, headers);

        ResponseEntity<UserResponse> response = testRestTemplate.exchange(
                "/user/patch/" + userId,
                HttpMethod.PATCH,
                entity,
                UserResponse.class
        );

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("List All User When is Valid Return List of Users")
    void listAllUser_WhenValid_ReturnListOfUsers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(loginAsAdmin());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<UserResponse>> response = testRestTemplate.exchange(
                "/user/list/all",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Get User By Id When is Valid Return User")
    void getUserById_WhenValid_ReturnUser() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(loginAsAdmin());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<UserResponse> response = testRestTemplate.exchange(
                "/user/" + adminId,
                HttpMethod.GET,
                entity,
                UserResponse.class
        );

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Verify Inactivate User Returns No Content")
    void deactivateUser_WhenValid_ReturnNoContent() {
        User savedUser = userRepository.save(UserCreator.createActiveUser());
        int userId = savedUser.getId();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(loginAsAdmin());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = testRestTemplate.exchange(
                "/user/"+ userId +"/deactivate",
                HttpMethod.PUT,
                entity,
                Void.class
        );

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("Verify Activate User Returns No Content")
    void activateUser_WhenValid_ReturnNoContent() {
        User savedUser = userRepository.save(UserCreator.createInactiveUser());
        int userId = savedUser.getId();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(loginAsAdmin());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = testRestTemplate.exchange(
                "/user/"+ userId +"/activate",
                HttpMethod.PUT,
                entity,
                Void.class
        );

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("Return Not Found When User Doesn't Have Valid Id")
    void activateUser_ReturnNotFound_WhenIdInvalid() {
        int nonExistentId = 999999;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(loginAsAdmin());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = testRestTemplate.exchange(
                "/user/"+ nonExistentId +"/activate",
                HttpMethod.PUT,
                entity,
                Void.class
        );

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Return Forbidden When User Is Not Admin")
    void activateUser_Return403_WhenUserIsNotAdmin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(loginAsUser());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = testRestTemplate.exchange(
                "/user/"+ userId +"/deactivate",
                HttpMethod.PUT,
                entity,
                Void.class
        );

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("Return Not Found When User Doesn't Have Valid Id")
    void deactivateUser_ReturnNotFound_WhenIdInvalid() {
        int nonExistentId = 999999;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(loginAsAdmin());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = testRestTemplate.exchange(
                "/user/" + nonExistentId + "/deactivate",
                HttpMethod.PUT,
                entity,
                Void.class
        );

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Return Forbidden When User Is Not Admin")
    void deactivateUser_Return403_WhenUserIsNotAdmin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(loginAsUser());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = testRestTemplate.exchange(
                "/user/"+ userId +"/deactivate",
                HttpMethod.PUT,
                entity,
                Void.class
        );

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private String loginAsAdmin() {
        LoginRequest loginRequest = new LoginRequest(ADMIN_USERNAME, ADMIN_PASSWORD);
        ResponseEntity<LoginResponse> response = testRestTemplate.postForEntity(
                "/auth/login",
                loginRequest,
                LoginResponse.class
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();

        return response.getBody().getToken();
    }

    private String loginAsUser() {
        LoginRequest loginRequest = new LoginRequest(USER_USERNAME, USER_PASSWORD);
        ResponseEntity<LoginResponse> response = testRestTemplate.postForEntity(
                "/auth/login",
                loginRequest,
                LoginResponse.class
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();

        return response.getBody().getToken();
    }

    private User seedAdminUser() {
        User user = new User();
        user.setName("adminn");
        user.setEmail(ADMIN_USERNAME);
        user.setAnniversaryDate(LocalDate.of(2008, 1, 1));
        user.setUserRole(UserRole.ADMIN);
        user.setDeleted(Boolean.FALSE);

        User savedUser = userRepository.save(user);

        Login login = new Login();
        login.setUsername(ADMIN_USERNAME);
        login.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
        login.setUser(savedUser);

        Login savedLogin = loginRepository.save(login);

        savedUser.setLogin(savedLogin);
        userRepository.save(savedUser);

        return userRepository.findById(savedUser.getId()).orElseThrow();
    }

    private User seedUser() {
        User user = new User();
        user.setName("adminn");
        user.setEmail(USER_USERNAME);
        user.setAnniversaryDate(LocalDate.of(2008, 1, 1));
        user.setUserRole(UserRole.USER);
        user.setDeleted(Boolean.FALSE);

        User savedUser = userRepository.save(user);

        Login login = new Login();
        login.setUsername(USER_USERNAME);
        login.setPassword(passwordEncoder.encode(USER_PASSWORD));
        login.setUser(savedUser);

        Login savedLogin = loginRepository.save(login);

        savedUser.setLogin(savedLogin);
        userRepository.save(savedUser);

        return userRepository.findById(savedUser.getId()).orElseThrow();
    }



}
