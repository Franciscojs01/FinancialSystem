package com.example.financialSystem.controllers;

import com.example.financialSystem.models.dto.requests.InvestmentPatchRequest;
import com.example.financialSystem.models.dto.requests.InvestmentRequest;
import com.example.financialSystem.models.dto.responses.InvestmentResponse;
import com.example.financialSystem.models.enums.InvestmentType;
import com.example.financialSystem.repositories.LoginRepository;
import com.example.financialSystem.services.InvestmentService;
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

@WebMvcTest(InvestmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class InvestmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InvestmentService investmentService;

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

    private InvestmentRequest investmentRequest;
    private InvestmentResponse investmentResponse;
    private InvestmentPatchRequest investmentPatchRequest;

    /**
     * Setup executed before each test.
     * Initializes the request and response objects used in tests.
     */
    @BeforeEach
    void setUp() {
        investmentRequest = new InvestmentRequest(
                InvestmentType.STOCK,
                new BigDecimal("1000.00"),
                LocalDate.of(2023, 1, 1),
                BenchMarkRate.EUR,
                2,
                "XP investments"
        );

        investmentResponse = new InvestmentResponse();
        investmentResponse.setId(1);
        investmentResponse.setValue(new BigDecimal("1000.00"));

        investmentPatchRequest = new InvestmentPatchRequest();
    }

    /**
     * Tests creating a new investment.
     * Verifies if POST /investments/create endpoint returns status 200 and the created investment.
     */
    @Test
    @DisplayName("Should create investment successfully and return status 200")
    void create_ShouldReturnCreatedInvestment_WhenSuccessful() throws Exception {
        when(investmentService.createInvestment(any(InvestmentRequest.class)))
                .thenReturn(investmentResponse);

        mockMvc.perform(post("/investments/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(investmentRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));

        verify(investmentService, times(1)).createInvestment(any(InvestmentRequest.class));
    }

    /**
     * Tests editing an existing investment.
     * Verifies if PUT /investments/edit/{id} endpoint correctly updates the investment.
     */
    @Test
    @DisplayName("Should edit investment successfully and return status 200")
    void edit_ShouldReturnUpdatedInvestment_WhenSuccessful() throws Exception {
        int investmentId = 1;

        when(investmentService.updateInvestment(anyInt(), any(InvestmentRequest.class)))
                .thenReturn(investmentResponse);

        mockMvc.perform(put("/investments/edit/{id}", investmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(investmentRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(investmentId));

        verify(investmentService, times(1)).updateInvestment(eq(investmentId), any(InvestmentRequest.class));
    }

    /**
     * Tests fetching an investment by ID.
     * Verifies if GET /investments/{id} endpoint returns the correct investment.
     */
    @Test
    @DisplayName("Should return investment by ID successfully")
    void getInvestment_ShouldReturnInvestment_WhenSuccessful() throws Exception {
        int investmentId = 1;

        when(investmentService.getInvestmentById(anyInt())).thenReturn(investmentResponse);

        mockMvc.perform(get("/investments/{id}", investmentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(investmentId));

        verify(investmentService, times(1)).getInvestmentById(investmentId);
    }

    /**
     * Tests investment yield simulation.
     * Verifies if GET /investments/{id}/simulate endpoint calculates yield correctly.
     */
    @Test
    @DisplayName("Should simulate investment successfully")
    void simulateInvestment_ShouldReturnSimulatedInvestment_WhenSuccessful() throws Exception {
        int investmentId = 1;
        int simulationDays = 30;

        investmentResponse.setValue(new BigDecimal("1050.00"));

        when(investmentService.simulateInvestment(anyInt(), anyInt())).thenReturn(investmentResponse);

        mockMvc.perform(get("/investments/{id}/simulate", investmentId)
                        .param("days", String.valueOf(simulationDays))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(investmentService, times(1)).simulateInvestment(investmentId, simulationDays);
    }

    /**
     * Tests listing authenticated user's investments.
     * Verifies if GET /investments/list/me endpoint returns only user's investments.
     */
    @Test
    @DisplayName("Should return all investments from authenticated user")
    void getAllInvestmentByUser_ShouldReturnUserInvestments_WhenSuccessful() throws Exception {
        List<InvestmentResponse> userInvestments = Arrays.asList(
                investmentResponse,
                new InvestmentResponse()
        );

        when(investmentService.listInvestments()).thenReturn(userInvestments);

        mockMvc.perform(get("/investments/list/me")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2));

        verify(investmentService, times(1)).listInvestments();
    }

    /**
     * Tests listing all system investments (Admin).
     * Verifies if GET /investments/list/all endpoint returns all investments.
     */
    @Test
    @DisplayName("Should return all system investments")
    void getAllInvestment_ShouldReturnAllInvestments_WhenSuccessful() throws Exception {
        List<InvestmentResponse> allInvestments = Arrays.asList(
                investmentResponse,
                new InvestmentResponse(),
                new InvestmentResponse()
        );

        when(investmentService.listAllInvestments()).thenReturn(allInvestments);

        mockMvc.perform(get("/investments/list/all")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(3));

        verify(investmentService, times(1)).listAllInvestments();
    }

    /**
     * Tests partial investment update.
     * Verifies if PATCH /investments/patch/{id} endpoint updates specific fields.
     */
    @Test
    @DisplayName("Should partially update investment successfully")
    void patchInvestment_ShouldReturnPatchedInvestment_WhenSuccessful() throws Exception {
        int investmentId = 1;

        when(investmentService.patchInvestment(anyInt(), any(InvestmentPatchRequest.class)))
                .thenReturn(investmentResponse);

        mockMvc.perform(patch("/investments/patch/{id}", investmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(investmentPatchRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(investmentService, times(1)).patchInvestment(eq(investmentId), any(InvestmentPatchRequest.class));
    }

    /**
     * Tests investment activation.
     * Verifies if PUT /investments/activate/{id} endpoint reactivates a deactivated investment.
     */
    @Test
    @DisplayName("Should activate investment successfully")
    void activateInvestment_ShouldReturnSuccessMessage_WhenSuccessful() throws Exception {
        int investmentId = 1;

        // Configures mock - void method returns nothing
        doNothing().when(investmentService).activateInvestment(anyInt());

        // Executes request and verifies success message
        mockMvc.perform(put("/investments/activate/{id}", investmentId))
                .andExpect(status().isOk())
                .andExpect(content().string("Investment activated with id: " + investmentId));

        verify(investmentService, times(1)).activateInvestment(investmentId);
    }

    /**
     * Tests investment deletion.
     * Verifies if DELETE /investments/delete/{id} endpoint removes the investment.
     */
    @Test
    @DisplayName("Should delete investment successfully")
    void deleteInvestment_ShouldReturnSuccessMessage_WhenSuccessful() throws Exception {
        int investmentId = 1;

        doNothing().when(investmentService).deleteInvestment(anyInt());

        mockMvc.perform(delete("/investments/delete/{id}", investmentId))
                .andExpect(status().isOk())
                .andExpect(content().string("Investment successfully deleted with id: " + investmentId));

        verify(investmentService, times(1)).deleteInvestment(investmentId);
    }
}
