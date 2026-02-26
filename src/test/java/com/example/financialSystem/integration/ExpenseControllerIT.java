package com.example.financialSystem.integration;

import com.example.financialSystem.models.dto.requests.ExpensePatchRequest;
import com.example.financialSystem.models.dto.requests.ExpenseRequest;
import com.example.financialSystem.models.dto.responses.ExpenseResponse;
import com.example.financialSystem.models.entity.Expense;
import com.example.financialSystem.models.entity.Login;
import com.example.financialSystem.models.entity.User;
import com.example.financialSystem.models.enums.ExpenseType;
import com.example.financialSystem.repositories.ExpenseRepository;
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
 * Integration tests for ExpenseController.
 * Tests the complete flow from HTTP request to database persistence.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
public class ExpenseControllerIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ExpenseRepository expenseRepository;

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
        expenseRepository.deleteAll();
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
    @DisplayName("Should create expense successfully when user is authenticated")
    void createExpense_ShouldReturnCreatedExpense_WhenSuccessful() {
        // Arrange
        ExpenseRequest request = new ExpenseRequest(
                ExpenseType.OTHER,
                new BigDecimal("250.00"),
                LocalDate.now(),
                BenchMarkRate.EUR,
                "Credit Card",
                true
        );

        HttpEntity<ExpenseRequest> entity = new HttpEntity<>(request, userHeaders);

        // Act
        ResponseEntity<ExpenseResponse> response = testRestTemplate.exchange(
                "/expense/create",
                HttpMethod.POST,
                entity,
                ExpenseResponse.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getExpenseType()).isEqualTo(ExpenseType.OTHER);
        assertThat(expenseRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should return expense by ID when expense exists")
    void getExpense_ShouldReturnExpense_WhenExpenseExists() {
        // Arrange
        Expense expense = createAndSaveExpense(savedUser);

        HttpEntity<Void> entity = new HttpEntity<>(userHeaders);

        // Act
        ResponseEntity<ExpenseResponse> response = testRestTemplate.exchange(
                "/expense/" + expense.getId(),
                HttpMethod.GET,
                entity,
                ExpenseResponse.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(expense.getId());
    }

    @Test
    @DisplayName("Should update expense successfully when user owns the expense")
    void editExpense_ShouldReturnUpdatedExpense_WhenUserOwnsExpense() {
        // Arrange - Create expense for the same user
        Expense expense = createAndSaveExpense(savedUser);

        ExpenseRequest updateRequest = new ExpenseRequest(
                ExpenseType.OTHER,
                new BigDecimal("300.00"),
                LocalDate.now(),
                BenchMarkRate.USD,
                "Debit Card",
                false
        );

        HttpEntity<ExpenseRequest> entity = new HttpEntity<>(updateRequest, userHeaders);

        // Act
        ResponseEntity<ExpenseResponse> response = testRestTemplate.exchange(
                "/expense/edit/" + expense.getId(),
                HttpMethod.PUT,
                entity,
                ExpenseResponse.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getPaymentMethod()).isEqualTo("Debit Card");
    }

    @Test
    @DisplayName("Should update expense successfully when admin edits any expense")
    void editExpense_ShouldReturnUpdatedExpense_WhenAdminEditsAnyExpense() {
        // Arrange - Create expense for regular user, but admin will edit it
        Expense expense = createAndSaveExpense(savedUser);

        ExpenseRequest updateRequest = new ExpenseRequest(
                ExpenseType.OTHER,
                new BigDecimal("500.00"),
                LocalDate.now(),
                BenchMarkRate.EUR,
                "Admin updated this expense",
                false
        );

        HttpEntity<ExpenseRequest> entity = new HttpEntity<>(updateRequest, adminHeaders);

        // Act
        ResponseEntity<ExpenseResponse> response = testRestTemplate.exchange(
                "/expense/edit/" + expense.getId(),
                HttpMethod.PUT,
                entity,
                ExpenseResponse.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("Should return 403 when user tries to edit another user's expense")
    void editExpense_ShouldReturn403_WhenUserEditsAnotherUserExpense() {
        // Arrange - Create expense for admin
        Expense adminExpense = createAndSaveExpense(savedAdmin);

        ExpenseRequest updateRequest = new ExpenseRequest(
                ExpenseType.OTHER,
                new BigDecimal("300.00"),
                LocalDate.now(),
                BenchMarkRate.USD,
                "Trying to update admin expense",
                false
        );

        HttpEntity<ExpenseRequest> entity = new HttpEntity<>(updateRequest, userHeaders);

        // Act
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/expense/edit/" + adminExpense.getId(),
                HttpMethod.PUT,
                entity,
                String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isIn(HttpStatus.FORBIDDEN, HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should return user expenses list when user is authenticated")
    void getAllExpenseByUser_ShouldReturnUserExpenses_WhenSuccessful() {
        // Arrange
        createAndSaveExpense(savedUser);
        createAndSaveExpense(savedUser);
        createAndSaveExpense(savedAdmin); // This should not appear in user's list

        HttpEntity<Void> entity = new HttpEntity<>(userHeaders);

        // Act
        ResponseEntity<List<ExpenseResponse>> response = testRestTemplate.exchange(
                "/expense/list/me",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<ExpenseResponse>>() {}
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    @DisplayName("Should return all expenses when admin requests")
    void getAllExpense_ShouldReturnAllExpenses_WhenAdminRequests() {
        // Arrange
        createAndSaveExpense(savedUser);
        createAndSaveExpense(savedUser);
        createAndSaveExpense(savedAdmin);

        HttpEntity<Void> entity = new HttpEntity<>(adminHeaders);

        // Act
        ResponseEntity<List<ExpenseResponse>> response = testRestTemplate.exchange(
                "/expense/list/all",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<ExpenseResponse>>() {}
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(3);
    }

    @Test
    @DisplayName("Should partially update expense when patch is successful")
    void patchExpense_ShouldReturnPatchedExpense_WhenSuccessful() {
        // Arrange
        Expense expense = createAndSaveExpense(savedUser);

        ExpensePatchRequest patchRequest = new ExpensePatchRequest();
        patchRequest.setPaymentMethod("Patched payment method");

        HttpEntity<ExpensePatchRequest> entity = new HttpEntity<>(patchRequest, userHeaders);

        // Act
        ResponseEntity<ExpenseResponse> response = testRestTemplate.exchange(
                "/expense/patch/" + expense.getId(),
                HttpMethod.PATCH,
                entity,
                ExpenseResponse.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("Should activate expense successfully when user owns the expense")
    void activateExpense_ShouldReturnSuccessMessage_WhenUserOwnsExpense() {
        // Arrange
        Expense expense = createAndSaveDeactivatedExpense(savedUser);

        HttpEntity<Void> entity = new HttpEntity<>(userHeaders);

        // Act
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/expense/activate/" + expense.getId(),
                HttpMethod.PUT,
                entity,
                String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Expense activated");
    }

    @Test
    @DisplayName("Should delete expense successfully")
    void deleteExpense_ShouldReturnSuccessMessage_WhenSuccessful() {
        // Arrange
        Expense expense = createAndSaveExpense(savedUser);

        HttpEntity<Void> entity = new HttpEntity<>(userHeaders);

        // Act
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/expense/delete/" + expense.getId(),
                HttpMethod.DELETE,
                entity,
                String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("successfully deleted");
    }

    @Test
    @DisplayName("Should return 403 when user tries to access another user's expense")
    void getExpense_ShouldReturn403_WhenUserAccessesAnotherUserExpense() {
        // Arrange
        Expense adminExpense = createAndSaveExpense(savedAdmin);

        HttpEntity<Void> entity = new HttpEntity<>(userHeaders);

        // Act
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/expense/" + adminExpense.getId(),
                HttpMethod.GET,
                entity,
                String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isIn(HttpStatus.FORBIDDEN, HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should return 403 when user tries to activate another user's expense")
    void activateExpense_ShouldReturn403_WhenUserActivatesAnotherUserExpense() {
        // Arrange
        Expense adminExpense = createAndSaveDeactivatedExpense(savedAdmin);

        HttpEntity<Void> entity = new HttpEntity<>(userHeaders);

        // Act
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/expense/activate/" + adminExpense.getId(),
                HttpMethod.PUT,
                entity,
                String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isIn(HttpStatus.FORBIDDEN, HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should return 401 when user is not authenticated")
    void createExpense_ShouldReturn401_WhenNotAuthenticated() {
        // Arrange
        ExpenseRequest request = new ExpenseRequest(
                ExpenseType.OTHER,
                new BigDecimal("250.00"),
                LocalDate.now(),
                BenchMarkRate.EUR,
                "Credit-card",
                true
        );

        HttpHeaders headersWithoutAuth = new HttpHeaders();
        headersWithoutAuth.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ExpenseRequest> entity = new HttpEntity<>(request, headersWithoutAuth);

        // Act
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/expense/create",
                HttpMethod.POST,
                entity,
                String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isIn(HttpStatus.UNAUTHORIZED, HttpStatus.FORBIDDEN);
    }

    /**
     * Helper method to create and save an expense for testing.
     */
    private Expense createAndSaveExpense(User user) {
        Expense expense = Expense.builder()
                .expenseType(ExpenseType.OTHER)
                .paymentMethod("Credit Card")
                .isFixed(false)
                .value(new BigDecimal("150.00"))
                .dateFinancial(LocalDate.now())
                .baseCurrency(BenchMarkRate.EUR)
                .user(user)
                .deleted(false)
                .build();
        return expenseRepository.save(expense);
    }

    private Expense createAndSaveDeactivatedExpense(User user) {
        Expense expense = Expense.builder()
                .expenseType(ExpenseType.OTHER)
                .paymentMethod("Credit Card")
                .isFixed(false)
                .value(new BigDecimal("150.00"))
                .dateFinancial(LocalDate.now())
                .baseCurrency(BenchMarkRate.EUR)
                .user(user)
                .deleted(true)
                .build();
        return expenseRepository.save(expense);
    }
}
