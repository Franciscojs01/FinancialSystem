package com.example.financialSystem.controllers;

import com.example.financialSystem.models.dto.requests.ExpensePatchRequest;
import com.example.financialSystem.models.dto.requests.ExpenseRequest;
import com.example.financialSystem.models.dto.responses.ExpenseResponse;
import com.example.financialSystem.models.enums.ExpenseType;
import com.example.financialSystem.repositories.LoginRepository;
import com.example.financialSystem.services.ExpenseService;
import com.example.financialSystem.services.TokenService;
import com.example.financialSystem.utils.BenchMarkRate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for ExpenseController.
 * Tests all endpoints related to expense management.
 * Uses MockMvc to simulate HTTP requests without needing a server.
 */
@WebMvcTest(ExpenseController.class)
@AutoConfigureMockMvc(addFilters = false)
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ExpenseService expenseService;

    /**
     * Mock of LoginRepository required by SecurityFilter.
     * Needed to satisfy Spring context dependencies.
     */
    @MockitoBean
    private LoginRepository loginRepository;

    /**
     * Mock of TokenService required by SecurityFilter.
     * Needed to satisfy Spring context dependencies.
     */
    @MockitoBean
    private TokenService tokenService;

    private ExpenseRequest expenseRequest;
    private ExpenseResponse expenseResponse;
    private ExpensePatchRequest expensePatchRequest;

    /**
     * Setup method executed before each test.
     * Initializes objects needed for tests.
     */
    @BeforeEach
    void setUp() {
        expenseRequest = new ExpenseRequest(
                ExpenseType.FOOD,
                new BigDecimal("500.00"),
                LocalDate.of(2022, 10, 5),
                BenchMarkRate.BRL,
                "Credit Card",
                false
        );

        expenseResponse = new ExpenseResponse();
        expenseResponse.setId(1);
        expenseResponse.setValue(new BigDecimal("500.00"));

        expensePatchRequest = new ExpensePatchRequest();
    }

    /**
     * Tests creating a new expense.
     * Endpoint: POST /expense/create
     * Expected: Status 200 and ExpenseResponse object in response body.
     */
    @Test
    @DisplayName("Should create expense successfully and return status 200")
    void createExpense_ShouldReturnCreatedExpense_WhenSuccessful() throws Exception {
        when(expenseService.createExpense(any(ExpenseRequest.class)))
                .thenReturn(expenseResponse);

        mockMvc.perform(post("/expense/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expenseRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(expenseService, times(1)).createExpense(any(ExpenseRequest.class));
    }

    /**
     * Tests full editing of an existing expense.
     * Endpoint: PUT /expense/edit/{id}
     * Expected: Status 200 and updated expense.
     */
    @Test
    @DisplayName("Should edit expense successfully and return status 200")
    void editExpense_ShouldReturnUpdatedExpense_WhenSuccessful() throws Exception {
        int expenseId = 1;

        when(expenseService.updateExpense(anyInt(), any(ExpenseRequest.class)))
                .thenReturn(expenseResponse);

        mockMvc.perform(put("/expense/edit/{id}", expenseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expenseRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expenseId));

        verify(expenseService, times(1)).updateExpense(eq(expenseId), any(ExpenseRequest.class));
    }

    /**
     * Tests fetching a specific expense by ID.
     * Endpoint: GET /expense/{id}
     * Expected: Status 200 and found expense data.
     */
    @Test
    @DisplayName("Should return expense by ID successfully")
    void getExpense_ShouldReturnExpense_WhenSuccessful() throws Exception {
        int expenseId = 1;

        when(expenseService.getExpenseById(anyInt())).thenReturn(expenseResponse);

        mockMvc.perform(get("/expense/{id}", expenseId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expenseId));

        verify(expenseService, times(1)).getExpenseById(expenseId);
    }

    /**
     * Tests listing logged user's expenses.
     * Endpoint: GET /expense/list/me
     * Expected: Status 200 and list of user's expenses.
     */
    @Test
    @DisplayName("Should return all expenses from authenticated user")
    void getAllExpenseByUser_ShouldReturnUserExpenses_WhenSuccessful() throws Exception {
        List<ExpenseResponse> userExpenses = Arrays.asList(
                expenseResponse,
                new ExpenseResponse()
        );

        when(expenseService.listExpense()).thenReturn(userExpenses);

        mockMvc.perform(get("/expense/list/me")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(expenseService, times(1)).listExpense();
    }

    /**
     * Tests listing all system expenses (Admin access).
     * Endpoint: GET /expense/list/all
     * Expected: Status 200 and complete list of expenses.
     */
    @Test
    @DisplayName("Should return all system expenses")
    void getAllExpense_ShouldReturnAllExpenses_WhenSuccessful() throws Exception {
        List<ExpenseResponse> allExpenses = Arrays.asList(
                expenseResponse,
                new ExpenseResponse(),
                new ExpenseResponse()
        );

        when(expenseService.listAllExpense()).thenReturn(allExpenses);

        mockMvc.perform(get("/expense/list/all")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));

        verify(expenseService, times(1)).listAllExpense();
    }

    /**
     * Tests partial expense update (PATCH).
     * Endpoint: PATCH /expense/patch/{id}
     * Expected: Status 200 and expense with updated fields.
     */
    @Test
    @DisplayName("Should partially update expense successfully")
    void patchExpense_ShouldReturnPatchedExpense_WhenSuccessful() throws Exception {
        int expenseId = 1;

        when(expenseService.patchExpense(anyInt(), any(ExpensePatchRequest.class)))
                .thenReturn(expenseResponse);

        mockMvc.perform(patch("/expense/patch/{id}", expenseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expensePatchRequest)))
                .andExpect(status().isOk());

        verify(expenseService, times(1)).patchExpense(eq(expenseId), any(ExpensePatchRequest.class));
    }

    /**
     * Tests activating a previously deactivated expense.
     * Endpoint: PUT /expense/activate/{id}
     * Expected: Status 200 and confirmation message.
     */
    @Test
    @DisplayName("Should activate expense successfully")
    void activateExpense_ShouldReturnSuccessMessage_WhenSuccessful() throws Exception {
        int expenseId = 1;

        doNothing().when(expenseService).activateExpense(anyInt());

        mockMvc.perform(put("/expense/activate/{id}", expenseId))
                .andExpect(status().isOk())
                .andExpect(content().string("Expense activated with id: " + expenseId));

        verify(expenseService, times(1)).activateExpense(expenseId);
    }

    /**
     * Tests expense deletion (soft/hard delete).
     * Endpoint: DELETE /expense/delete/{id}
     * Expected: Status 200 and deletion confirmation message.
     */
    @Test
    @DisplayName("Should delete expense successfully")
    void deleteExpense_ShouldReturnSuccessMessage_WhenSuccessful() throws Exception {
        int expenseId = 1;

        doNothing().when(expenseService).deleteExpense(anyInt());

        mockMvc.perform(delete("/expense/delete/{id}", expenseId))
                .andExpect(status().isOk())
                .andExpect(content().string("Expense successfully deleted"));

        verify(expenseService, times(1)).deleteExpense(expenseId);
    }
}
