package com.example.financialSystem.controllers;

import com.example.financialSystem.models.dto.requests.CostPatchRequest;
import com.example.financialSystem.models.dto.requests.CostRequest;
import com.example.financialSystem.models.dto.responses.CostResponse;
import com.example.financialSystem.models.enums.CostType;
import com.example.financialSystem.repositories.LoginRepository;
import com.example.financialSystem.services.CostService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for CostController.
 * Responsible for testing all cost management endpoints.
 *
 * Annotations used:
 * - @WebMvcTest: Loads only the specified controller context
 * - @AutoConfigureMockMvc: Configures MockMvc automatically
 * - addFilters = false: Disables security filters to simplify tests
 */
@WebMvcTest(CostController.class)
@AutoConfigureMockMvc(addFilters = false)
class CostControllerTest {

    /**
     * MockMvc is the main tool for testing Spring MVC controllers.
     * Allows simulating HTTP requests and verifying responses.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * ObjectMapper converts Java objects to JSON and vice versa.
     * Essential for sending and receiving data in request bodies.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Mock of CostService to simulate the service layer behavior.
     * This isolates the test, focusing only on controller behavior.
     */
    @MockitoBean
    private CostService costService;

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

    // Test objects for reuse between methods
    private CostRequest costRequest;
    private CostResponse costResponse;
    private CostPatchRequest costPatchRequest;

    /**
     * Setup executed before each test.
     * Initializes request and response objects with valid data.
     */
    @BeforeEach
    void setUp() {
        // Initializes request with valid data
        costRequest = new CostRequest(
                CostType.OTHER,
                "Test cost creation",
                new BigDecimal("250.00"),
                LocalDate.of(2024, 1, 2),
                BenchMarkRate.EUR
        );

        // Configures response with example data
        costResponse = new CostResponse();
        costResponse.setId(1);
        costResponse.setCostType(CostType.OTHER);
        costResponse.setValue(new BigDecimal("250.00"));

        // Initializes request for patch operations
        costPatchRequest = new CostPatchRequest();
    }

    /**
     * Tests the cost creation endpoint.
     * Endpoint: POST /cost/create
     *
     * Scenario: Sending valid data for creating a new cost.
     * Expected result: Status 200 and CostResponse object in body.
     */
    @Test
    @DisplayName("Should create a new cost and return it when successful")
    void createCost_ShouldReturnCreatedCost_WhenSuccessful() throws Exception {
        // Arrange: Defines the expected mock behavior
        when(costService.createCost(any(CostRequest.class))).thenReturn(costResponse);

        // Act & Assert: Executes the request and validates the response
        mockMvc.perform(post("/cost/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(costRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.costType").value("OTHER"));

        // Verify: Confirms that the service was called correctly
        verify(costService, times(1)).createCost(any(CostRequest.class));
    }

    /**
     * Tests the cost search by ID endpoint.
     * Endpoint: GET /cost/{id}
     *
     * Scenario: Searching for an existing cost by its identifier.
     * Expected result: Status 200 and found cost data.
     */
    @Test
    @DisplayName("Should return cost details when cost is found by ID")
    void getCost_ShouldReturnCost_WhenSuccessful() throws Exception {
        int costId = 1;

        // Configures mock to return the cost when searched
        when(costService.getCostById(anyInt())).thenReturn(costResponse);

        // Executes GET and validates returned fields
        mockMvc.perform(get("/cost/{id}", costId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(costId));

        // Verifies if service was called with correct ID
        verify(costService, times(1)).getCostById(costId);
    }

    /**
     * Tests the full cost edit endpoint.
     * Endpoint: PUT /cost/edit/{id}
     *
     * Scenario: Updating all fields of an existing cost.
     * Expected result: Status 200 and updated cost.
     */
    @Test
    @DisplayName("Should update cost details when edit is successful")
    void editCost_ShouldReturnUpdatedCost_WhenSuccessful() throws Exception {
        int costId = 1;

        // Mock returns updated cost after editing
        when(costService.updateCost(anyInt(), any(CostRequest.class))).thenReturn(costResponse);

        // Executes PUT with new content
        mockMvc.perform(put("/cost/edit/{id}", costId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(costRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(costId));

        // Verifies call with correct parameters
        verify(costService, times(1)).updateCost(eq(costId), any(CostRequest.class));
    }

    /**
     * Tests the user costs listing endpoint.
     * Endpoint: GET /cost/list/me
     *
     * Scenario: Authenticated user requests their own costs.
     * Expected result: Status 200 and list of user costs.
     */
    @Test
    @DisplayName("Should return list of user costs when successful")
    void getAllCostByUser_ShouldReturnUserCosts_WhenSuccessful() throws Exception {
        // Prepares user costs list
        List<CostResponse> userCosts = Arrays.asList(costResponse, new CostResponse());

        when(costService.listCost()).thenReturn(userCosts);

        // Verifies returned list size
        mockMvc.perform(get("/cost/list/me")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2));

        verify(costService, times(1)).listCost();
    }

    /**
     * Tests the all costs listing endpoint (Admin).
     * Endpoint: GET /cost/list/all
     *
     * Scenario: Admin requests complete system costs list.
     * Expected result: Status 200 and list with all costs.
     */
    @Test
    @DisplayName("Should return all system costs when successful")
    void getAllCost_ShouldReturnAllCosts_WhenSuccessful() throws Exception {
        // Prepares list with all system costs
        List<CostResponse> allCosts = Arrays.asList(
                costResponse,
                new CostResponse(),
                new CostResponse()
        );

        when(costService.listAllCost()).thenReturn(allCosts);

        // Verifies complete return
        mockMvc.perform(get("/cost/list/all")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(3));

        verify(costService, times(1)).listAllCost();
    }

    /**
     * Tests the partial update endpoint (PATCH).
     * Endpoint: PATCH /cost/patch/{id}
     *
     * Scenario: Updating only some cost fields.
     * Expected result: Status 200 and cost with modified fields.
     */
    @Test
    @DisplayName("Should partially update cost when successful")
    void patchCost_ShouldReturnPatchedCost_WhenSuccessful() throws Exception {
        int costId = 1;

        // Configures mock for patch operation
        when(costService.patchCost(anyInt(), any(CostPatchRequest.class))).thenReturn(costResponse);

        // Executes PATCH request
        mockMvc.perform(patch("/cost/patch/{id}", costId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(costPatchRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(costService, times(1)).patchCost(eq(costId), any(CostPatchRequest.class));
    }

    /**
     * Tests the cost activation endpoint.
     * Endpoint: PUT /cost/activate/{id}
     *
     * Scenario: Reactivating a previously deactivated cost.
     * Expected result: Status 200 and confirmation message.
     */
    @Test
    @DisplayName("Should activate cost when successful")
    void activateCost_ShouldReturnSuccessMessage_WhenSuccessful() throws Exception {
        int costId = 1;

        // For void methods, we use doNothing()
        doNothing().when(costService).activateCost(anyInt());

        // Verifies return message
        mockMvc.perform(put("/cost/activate/{id}", costId))
                .andExpect(status().isOk())
                .andExpect(content().string("Cost activated with id: " + costId));

        verify(costService, times(1)).activateCost(costId);
    }

    /**
     * Tests the cost deletion endpoint.
     * Endpoint: DELETE /cost/delete/{id}
     *
     * Scenario: Deleting (soft or hard delete) a cost.
     * Expected result: Status 200 and confirmation message.
     */
    @Test
    @DisplayName("Should delete cost when successful")
    void deleteCost_ShouldReturnSuccessMessage_WhenSuccessful() throws Exception {
        int costId = 1;

        // Configures mock for delete method
        doNothing().when(costService).deleteCost(anyInt());

        // Executes DELETE and validates message
        mockMvc.perform(delete("/cost/delete/{id}", costId))
                .andExpect(status().isOk())
                .andExpect(content().string("Cost with id " + costId + " successfully deleted"));

        verify(costService, times(1)).deleteCost(costId);
    }
}
