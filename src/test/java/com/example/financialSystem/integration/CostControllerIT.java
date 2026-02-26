package com.example.financialSystem.integration;

import com.example.financialSystem.models.dto.requests.CostPatchRequest;
import com.example.financialSystem.models.dto.requests.CostRequest;
import com.example.financialSystem.models.dto.responses.CostResponse;
import com.example.financialSystem.models.entity.Cost;
import com.example.financialSystem.models.entity.Login;
import com.example.financialSystem.models.entity.User;
import com.example.financialSystem.models.enums.CostType;
import com.example.financialSystem.repositories.CostRepository;
import com.example.financialSystem.repositories.LoginRepository;
import com.example.financialSystem.repositories.UserRepository;
import com.example.financialSystem.services.TokenService;
import com.example.financialSystem.utils.BenchMarkRate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.example.financialSystem.models.enums.UserRole.ADMIN;
import static com.example.financialSystem.models.enums.UserRole.USER;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for CostController.
 * Tests the complete flow from HTTP request to database persistence.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
public class CostControllerIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private CostRepository costRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private TokenService tokenService;

    private User savedUser;
    private User savedAdmin;
    private HttpHeaders userHeaders;
    private HttpHeaders adminHeaders;
    private Login savedUserLogin;
    private Login savedAdminLogin;

    @BeforeEach
    void setUp() {
        // Clean up repositories
        costRepository.deleteAll();
        loginRepository.deleteAll();
        userRepository.deleteAll();

        // Create and save regular user
        User user = new User();
        user.setName("Test User");
        user.setEmail("user@test.com");
        user.setUserRole(USER);
        savedUser = userRepository.save(user);

        // Create login for regular user
        Login userLogin = new Login();
        userLogin.setUsername("testuser");
        userLogin.setPassword(new BCryptPasswordEncoder().encode("password123"));
        userLogin.setUser(savedUser);
        savedUserLogin = loginRepository.save(userLogin);

        // Create and save admin user
        User admin = new User();
        admin.setName("Admin User");
        admin.setEmail("admin@test.com");
        admin.setUserRole(ADMIN);
        savedAdmin = userRepository.save(admin);

        // Create login for admin
        Login adminLogin = new Login();
        adminLogin.setUsername("adminuser");
        adminLogin.setPassword(new BCryptPasswordEncoder().encode("admin123"));
        adminLogin.setUser(savedAdmin);
        savedAdminLogin = loginRepository.save(adminLogin);

        // Generate tokens
        String userToken = tokenService.generateToken(savedUserLogin);
        String adminToken = tokenService.generateToken(savedAdminLogin);

        // Setup headers with authentication
        userHeaders = new HttpHeaders();
        userHeaders.setContentType(MediaType.APPLICATION_JSON);
        userHeaders.setBearerAuth(userToken);

        adminHeaders = new HttpHeaders();
        adminHeaders.setContentType(MediaType.APPLICATION_JSON);
        adminHeaders.setBearerAuth(adminToken);
    }

    @Test
    @DisplayName("Should create cost successfully when user is authenticated")
    void createCost_ShouldReturnCreatedCost_WhenSuccessful() {
        // Arrange
        CostRequest request = new CostRequest(
                CostType.OTHER,
                "Test cost",
                new BigDecimal("150.00"),
                LocalDate.now(),
                BenchMarkRate.EUR
        );

        HttpEntity<CostRequest> entity = new HttpEntity<>(request, userHeaders);

        // Act
        ResponseEntity<CostResponse> response = testRestTemplate.exchange(
                "/cost/create",
                HttpMethod.POST,
                entity,
                CostResponse.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCostType()).isEqualTo(CostType.OTHER);
        assertThat(costRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should return cost by ID when cost exists")
    void getCost_ShouldReturnCost_WhenCostExists() {
        // Arrange
        Cost cost = createAndSaveCost(savedUser);

        HttpEntity<Void> entity = new HttpEntity<>(userHeaders);

        // Act
        ResponseEntity<CostResponse> response = testRestTemplate.exchange(
                "/cost/" + cost.getId(),
                HttpMethod.GET,
                entity,
                CostResponse.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(cost.getId());
    }

    @Test
    @DisplayName("Should update cost successfully when admin updates")
    void editCost_ShouldReturnUpdatedCost_WhenAdminUpdates() {
        // Arrange - Create cost for admin user
        Cost cost = createAndSaveCost(savedAdmin);

        CostRequest updateRequest = new CostRequest(
                CostType.OTHER,
                "Updated cost description",
                new BigDecimal("200.00"),
                LocalDate.now(),
                BenchMarkRate.USD
        );

        HttpEntity<CostRequest> entity = new HttpEntity<>(updateRequest, adminHeaders);

        // Act
        ResponseEntity<CostResponse> response = testRestTemplate.exchange(
                "/cost/edit/" + cost.getId(),
                HttpMethod.PUT,
                entity,
                CostResponse.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getObservation()).isEqualTo(updateRequest.observation());
    }

    @Test
    @DisplayName("Should update cost successfully when user owns the cost")
    void editCost_ShouldReturnUpdatedCost_WhenUserOwnsCost() {
        // Arrange - Create cost for the same user
        Cost cost = createAndSaveCost(savedUser);

        CostRequest updateRequest = new CostRequest(
                CostType.OTHER,
                "Updated cost description",
                new BigDecimal("200.00"),
                LocalDate.now(),
                BenchMarkRate.USD
        );

        HttpEntity<CostRequest> entity = new HttpEntity<>(updateRequest, userHeaders);

        // Act
        ResponseEntity<CostResponse> response = testRestTemplate.exchange(
                "/cost/edit/" + cost.getId(),
                HttpMethod.PUT,
                entity,
                CostResponse.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getObservation()).isEqualTo("Updated cost description");
    }

    @Test
    @DisplayName("Should update cost successfully when admin edits any cost")
    void editCost_ShouldReturnUpdatedCost_WhenAdminEditsAnyCost() {
        // Arrange - Create cost for regular user, but admin will edit it
        Cost cost = createAndSaveCost(savedUser);

        CostRequest updateRequest = new CostRequest(
                CostType.OTHER,
                "Admin updated this cost",
                new BigDecimal("300.00"),
                LocalDate.now(),
                BenchMarkRate.USD
        );

        HttpEntity<CostRequest> entity = new HttpEntity<>(updateRequest, adminHeaders);

        // Act
        ResponseEntity<CostResponse> response = testRestTemplate.exchange(
                "/cost/edit/" + cost.getId(),
                HttpMethod.PUT,
                entity,
                CostResponse.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("Should return user costs list when user is authenticated")
    void getAllCostByUser_ShouldReturnUserCosts_WhenSuccessful() {
        // Arrange
        createAndSaveCost(savedUser);
        createAndSaveCost(savedUser);
        createAndSaveCost(savedAdmin); // This should not appear in user's list

        HttpEntity<Void> entity = new HttpEntity<>(userHeaders);

        // Act
        ResponseEntity<List<CostResponse>> response = testRestTemplate.exchange(
                "/cost/list/me",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<CostResponse>>() {}
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    @DisplayName("Should return all costs when admin requests")
    void getAllCost_ShouldReturnAllCosts_WhenAdminRequests() {
        // Arrange
        createAndSaveCost(savedUser);
        createAndSaveCost(savedUser);
        createAndSaveCost(savedAdmin);

        HttpEntity<Void> entity = new HttpEntity<>(adminHeaders);

        // Act
        ResponseEntity<List<CostResponse>> response = testRestTemplate.exchange(
                "/cost/list/all",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<CostResponse>>() {}
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(3);
    }

    @Test
    @DisplayName("Should partially update cost when patch is successful")
    void patchCost_ShouldReturnPatchedCost_WhenSuccessful() {
        // Arrange
        Cost cost = createAndSaveCost(savedUser);

        CostPatchRequest patchRequest = new CostPatchRequest();
        patchRequest.setObservation("Patched description");

        HttpEntity<CostPatchRequest> entity = new HttpEntity<>(patchRequest, userHeaders);

        // Act
        ResponseEntity<CostResponse> response = testRestTemplate.exchange(
                "/cost/patch/" + cost.getId(),
                HttpMethod.PATCH,
                entity,
                CostResponse.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("Should activate cost successfully when user owns the cost")
    void activateCost_ShouldReturnSuccessMessage_WhenUserOwnsCost() {
        // Arrange - Create deactivated cost for the user
        Cost cost = createAndSaveDeactivateCost(savedUser);

        HttpEntity<Void> entity = new HttpEntity<>(userHeaders);

        // Act
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/cost/activate/" + cost.getId(),
                HttpMethod.PUT,
                entity,
                String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Should activate cost successfully when admin activates")
    void activateCost_ShouldReturnSuccessMessage_WhenAdminActivates() {
        // Arrange - Create deactivated cost for admin
        Cost cost = createAndSaveDeactivateCost(savedAdmin);

        HttpEntity<Void> entity = new HttpEntity<>(adminHeaders);

        // Act
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/cost/activate/" + cost.getId(),
                HttpMethod.PUT,
                entity,
                String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Should delete cost successfully")
    void deleteCost_ShouldReturnSuccessMessage_WhenSuccessful() {
        // Arrange
        Cost cost = createAndSaveCost(savedUser);

        HttpEntity<Void> entity = new HttpEntity<>(userHeaders);

        // Act
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/cost/delete/" + cost.getId(),
                HttpMethod.DELETE,
                entity,
                String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("successfully deleted");
    }

    @Test
    @DisplayName("Should return 403 when user tries to access another user's cost")
    void getCost_ShouldReturn403_WhenUserAccessesAnotherUserCost() {
        // Arrange
        Cost adminCost = createAndSaveCost(savedAdmin);

        HttpEntity<Void> entity = new HttpEntity<>(userHeaders);

        // Act
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/cost/" + adminCost.getId(),
                HttpMethod.GET,
                entity,
                String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isIn(HttpStatus.FORBIDDEN, HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should return 403 when user tries to edit another user's cost")
    void editCost_ShouldReturn403_WhenUserEditsAnotherUserCost() {
        // Arrange - Create cost for admin
        Cost adminCost = createAndSaveCost(savedAdmin);

        CostRequest updateRequest = new CostRequest(
                CostType.OTHER,
                "Trying to update admin cost",
                new BigDecimal("200.00"),
                LocalDate.now(),
                BenchMarkRate.USD
        );

        HttpEntity<CostRequest> entity = new HttpEntity<>(updateRequest, userHeaders);

        // Act
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/cost/edit/" + adminCost.getId(),
                HttpMethod.PUT,
                entity,
                String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isIn(HttpStatus.FORBIDDEN, HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should return 403 when user tries to activate another user's cost")
    void activateCost_ShouldReturn403_WhenUserActivatesAnotherUserCost() {
        // Arrange - Create deactivated cost for admin
        Cost adminCost = createAndSaveDeactivateCost(savedAdmin);

        HttpEntity<Void> entity = new HttpEntity<>(userHeaders);

        // Act
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/cost/activate/" + adminCost.getId(),
                HttpMethod.PUT,
                entity,
                String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isIn(HttpStatus.FORBIDDEN, HttpStatus.NOT_FOUND);
    }

    /**
     * Helper method to create and save a cost for testing.
     */
    private Cost createAndSaveCost(User user) {
        Cost cost = Cost.builder()
                .costType(CostType.OTHER)
                .observation("Test cost observation")
                .value(new BigDecimal("100.00"))
                .dateFinancial(LocalDate.now())
                .baseCurrency(BenchMarkRate.EUR)
                .user(user)
                .deleted(false)
                .build();
        return costRepository.save(cost);
    }

    private Cost createAndSaveDeactivateCost(User user) {
        Cost cost = Cost.builder()
                .costType(CostType.OTHER)
                .observation("Test cost observation")
                .value(new BigDecimal("100.00"))
                .dateFinancial(LocalDate.now())
                .baseCurrency(BenchMarkRate.EUR)
                .user(user)
                .deleted(true)
                .build();
        return costRepository.save(cost);
    }
}
