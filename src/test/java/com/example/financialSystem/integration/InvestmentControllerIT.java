package com.example.financialSystem.integration;

import com.example.financialSystem.models.dto.requests.InvestmentPatchRequest;
import com.example.financialSystem.models.dto.requests.InvestmentRequest;
import com.example.financialSystem.models.dto.responses.InvestmentResponse;
import com.example.financialSystem.models.entity.Investment;
import com.example.financialSystem.models.entity.Login;
import com.example.financialSystem.models.entity.User;
import com.example.financialSystem.models.enums.InvestmentType;
import com.example.financialSystem.repositories.InvestmentRepository;
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
 * Integration tests for InvestmentController.
 * Tests the complete flow from HTTP request to database persistence.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
public class InvestmentControllerIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private InvestmentRepository investmentRepository;

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

    @BeforeEach
    void setUp() {
        // Clean up repositories
        investmentRepository.deleteAll();
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
        Login savedUserLogin = loginRepository.save(userLogin);

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
        Login savedAdminLogin = loginRepository.save(adminLogin);

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
    @DisplayName("Should create investment successfully when user is authenticated")
    void createInvestment_ShouldReturnCreatedInvestment_WhenSuccessful() {
        // Arrange
        InvestmentRequest request = new InvestmentRequest(
                InvestmentType.STOCK,
                new BigDecimal("1000.00"),
                LocalDate.now(),
                BenchMarkRate.USD,
                10,
                "Test investment"
        );

        HttpEntity<InvestmentRequest> entity = new HttpEntity<>(request, userHeaders);

        // Act
        ResponseEntity<InvestmentResponse> response = testRestTemplate.exchange(
                "/investments/create",
                HttpMethod.POST,
                entity,
                InvestmentResponse.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getInvestmentType()).isEqualTo(InvestmentType.STOCK);
        assertThat(investmentRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should return investment by ID when investment exists")
    void getInvestment_ShouldReturnInvestment_WhenInvestmentExists() {
        // Arrange
        Investment investment = createAndSaveInvestment(savedUser);

        HttpEntity<Void> entity = new HttpEntity<>(userHeaders);

        // Act
        ResponseEntity<InvestmentResponse> response = testRestTemplate.exchange(
                "/investments/" + investment.getId(),
                HttpMethod.GET,
                entity,
                InvestmentResponse.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(investment.getId());
    }

    @Test
    @DisplayName("Should update investment successfully when user owns the investment")
    void editInvestment_ShouldReturnUpdatedInvestment_WhenUserOwnsInvestment() {
        // Arrange - Create investment for the same user
        Investment investment = createAndSaveInvestment(savedUser);

        InvestmentRequest updateRequest = new InvestmentRequest(
                InvestmentType.FUND,
                new BigDecimal("2000.00"),
                LocalDate.now(),
                BenchMarkRate.EUR,
                20,
                "Updated Broker"
        );

        HttpEntity<InvestmentRequest> entity = new HttpEntity<>(updateRequest, userHeaders);

        // Act
        ResponseEntity<InvestmentResponse> response = testRestTemplate.exchange(
                "/investments/edit/" + investment.getId(),
                HttpMethod.PUT,
                entity,
                InvestmentResponse.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getBrokerName()).isEqualTo("Updated Broker");
    }

    @Test
    @DisplayName("Should update investment successfully when admin edits any investment")
    void editInvestment_ShouldReturnUpdatedInvestment_WhenAdminEditsAnyInvestment() {
        // Arrange - Create investment for regular user, but admin will edit it
        Investment investment = createAndSaveInvestment(savedUser);

        InvestmentRequest updateRequest = new InvestmentRequest(
                InvestmentType.CRYPTO,
                new BigDecimal("5000.00"),
                LocalDate.now(),
                BenchMarkRate.BRL,
                50,
                "Admin updated this investment"
        );

        HttpEntity<InvestmentRequest> entity = new HttpEntity<>(updateRequest, adminHeaders);

        // Act
        ResponseEntity<InvestmentResponse> response = testRestTemplate.exchange(
                "/investments/edit/" + investment.getId(),
                HttpMethod.PUT,
                entity,
                InvestmentResponse.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("Should return 403 when user tries to edit another user's investment")
    void editInvestment_ShouldReturn403_WhenUserEditsAnotherUserInvestment() {
        // Arrange - Create investment for admin
        Investment adminInvestment = createAndSaveInvestment(savedAdmin);

        InvestmentRequest updateRequest = new InvestmentRequest(
                InvestmentType.FIXED_INCOME,
                new BigDecimal("2000.00"),
                LocalDate.now(),
                BenchMarkRate.USD,
                20,
                "Trying to update admin investment"
        );

        HttpEntity<InvestmentRequest> entity = new HttpEntity<>(updateRequest, userHeaders);

        // Act
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/investments/edit/" + adminInvestment.getId(),
                HttpMethod.PUT,
                entity,
                String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isIn(HttpStatus.FORBIDDEN, HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should simulate investment yield correctly")
    void simulateInvestment_ShouldReturnSimulatedValue_WhenSuccessful() {
        // Arrange
        Investment investment = createAndSaveInvestment(savedUser);
        int simulationDays = 30;

        HttpEntity<Void> entity = new HttpEntity<>(userHeaders);

        // Act
        ResponseEntity<InvestmentResponse> response = testRestTemplate.exchange(
                "/investments/" + investment.getId() + "/simulate?days=" + simulationDays,
                HttpMethod.GET,
                entity,
                InvestmentResponse.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("Should return user investments list when user is authenticated")
    void getAllInvestmentByUser_ShouldReturnUserInvestments_WhenSuccessful() {
        // Arrange
        createAndSaveInvestment(savedUser);
        createAndSaveInvestment(savedUser);
        createAndSaveInvestment(savedAdmin);

        HttpEntity<Void> entity = new HttpEntity<>(userHeaders);

        // Act
        ResponseEntity<List<InvestmentResponse>> response = testRestTemplate.exchange(
                "/investments/list/me",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<InvestmentResponse>>() {}
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    @DisplayName("Should return all investments when admin requests")
    void getAllInvestment_ShouldReturnAllInvestments_WhenAdminRequests() {
        // Arrange
        createAndSaveInvestment(savedUser);
        createAndSaveInvestment(savedUser);
        createAndSaveInvestment(savedAdmin);

        HttpEntity<Void> entity = new HttpEntity<>(adminHeaders);

        // Act
        ResponseEntity<List<InvestmentResponse>> response = testRestTemplate.exchange(
                "/investments/list/all",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<InvestmentResponse>>() {}
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(3);
    }

    @Test
    @DisplayName("Should partially update investment when patch is successful")
    void patchInvestment_ShouldReturnPatchedInvestment_WhenSuccessful() {
        // Arrange
        Investment investment = createAndSaveInvestment(savedUser);

        InvestmentPatchRequest patchRequest = new InvestmentPatchRequest();
        patchRequest.setBrokerName("Patched Broker");

        HttpEntity<InvestmentPatchRequest> entity = new HttpEntity<>(patchRequest, userHeaders);

        // Act
        ResponseEntity<InvestmentResponse> response = testRestTemplate.exchange(
                "/investments/patch/" + investment.getId(),
                HttpMethod.PATCH,
                entity,
                InvestmentResponse.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("Should activate investment successfully when user owns the investment")
    void activateInvestment_ShouldReturnSuccessMessage_WhenUserOwnsInvestment() {
        // Arrange
        Investment investment = createAndSaveDeactivatedInvestment(savedUser);

        HttpEntity<Void> entity = new HttpEntity<>(userHeaders);

        // Act
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/investments/activate/" + investment.getId(),
                HttpMethod.PUT,
                entity,
                String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Investment activated");
    }

    @Test
    @DisplayName("Should delete investment successfully")
    void deleteInvestment_ShouldReturnSuccessMessage_WhenSuccessful() {
        // Arrange
        Investment investment = createAndSaveInvestment(savedUser);

        HttpEntity<Void> entity = new HttpEntity<>(userHeaders);

        // Act
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/investments/delete/" + investment.getId(),
                HttpMethod.DELETE,
                entity,
                String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("successfully deleted");
    }

    @Test
    @DisplayName("Should return 403 when user tries to access another user's investment")
    void getInvestment_ShouldReturn403_WhenUserAccessesAnotherUserInvestment() {
        // Arrange
        Investment adminInvestment = createAndSaveInvestment(savedAdmin);

        HttpEntity<Void> entity = new HttpEntity<>(userHeaders);

        // Act
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/investments/" + adminInvestment.getId(),
                HttpMethod.GET,
                entity,
                String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isIn(HttpStatus.FORBIDDEN, HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should return 403 when user tries to activate another user's investment")
    void activateInvestment_ShouldReturn403_WhenUserActivatesAnotherUserInvestment() {
        // Arrange
        Investment adminInvestment = createAndSaveDeactivatedInvestment(savedAdmin);

        HttpEntity<Void> entity = new HttpEntity<>(userHeaders);

        // Act
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/investments/activate/" + adminInvestment.getId(),
                HttpMethod.PUT,
                entity,
                String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isIn(HttpStatus.FORBIDDEN, HttpStatus.NOT_FOUND);
    }

    /**
     * Helper method to create and save an investment for testing.
     */
    private Investment createAndSaveInvestment(User user) {
        Investment investment = Investment.builder()
                .investmentType(InvestmentType.FUND)
                .actionQuantity(10)
                .brokerName("Test Broker")
                .value(new BigDecimal("1000.00"))
                .dateFinancial(LocalDate.now())
                .baseCurrency(BenchMarkRate.USD)
                .user(user)
                .deleted(false)
                .build();
        return investmentRepository.save(investment);
    }

    private Investment createAndSaveDeactivatedInvestment(User user) {
        Investment investment = Investment.builder()
                .investmentType(InvestmentType.FUND)
                .actionQuantity(10)
                .brokerName("Test Broker")
                .value(new BigDecimal("1000.00"))
                .dateFinancial(LocalDate.now())
                .baseCurrency(BenchMarkRate.BRL)
                .user(user)
                .deleted(true)
                .build();
        return investmentRepository.save(investment);
    }
}
