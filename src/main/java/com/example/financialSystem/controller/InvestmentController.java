package com.example.financialSystem.controller;

import com.example.financialSystem.exception.handler.ExceptionDetails;
import com.example.financialSystem.model.dto.requests.InvestmentPatchRequest;
import com.example.financialSystem.model.dto.requests.InvestmentRequest;
import com.example.financialSystem.model.dto.responses.InvestmentResponse;
import com.example.financialSystem.service.InvestmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/investments")
@Tag(name = "Investments", description = "Endpoints for managing and simulating financial investments")
public class InvestmentController {

    private final InvestmentService investmentService;

    public InvestmentController(InvestmentService investmentService) {
        this.investmentService = investmentService;
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new investment", description = "Registers a new investment in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Investment created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ExceptionDetails.class)))
    })
    public ResponseEntity<InvestmentResponse> create(@Valid @RequestBody InvestmentRequest investmentRequest) {
        return ResponseEntity.ok().body(investmentService.createInvestment(investmentRequest));
    }

    @PutMapping(value = "/edit/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update investment", description = "Updates all fields of an existing investment.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Investment updated successfully"),
            @ApiResponse(responseCode = "404", description = "Investment not found",
                    content = @Content(schema = @Schema(implementation = ExceptionDetails.class)))
    })
    public ResponseEntity<InvestmentResponse> edit(@PathVariable int id, @Valid @RequestBody InvestmentRequest investmentRequest) {
        return ResponseEntity.ok().body(investmentService.updateInvestment(id, investmentRequest));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get investment by ID", description = "Returns the details of a specific investment.")
    @ApiResponse(responseCode = "200", description = "Investment found")
    public ResponseEntity<InvestmentResponse> getInvestment(@PathVariable int id) {
        return ResponseEntity.ok().body(investmentService.getInvestmentById(id));
    }

    @GetMapping(value = "/{id}/simulate", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Simulate investment yield", description = "Calculates the projected yield for a specific investment over a number of days.")
    @ApiResponse(responseCode = "200", description = "Simulation completed")
    public ResponseEntity<InvestmentResponse> simulateInvestment(
            @PathVariable int id,
            @Parameter(description = "Number of days for the simulation") @RequestParam int days) {
        return ResponseEntity.ok().body(investmentService.simulateInvestment(id, days));
    }

    @GetMapping(value = "/list/me", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List my investments", description = "Returns all investments belonging to the authenticated user.")
    @ApiResponse(responseCode = "200", description = "User investments retrieved")
    public ResponseEntity<List<InvestmentResponse>> getAllInvestmentByUser() {
        return ResponseEntity.ok().body(investmentService.listInvestments());
    }

    @GetMapping(value = "/list/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List all system investments (Admin)", description = "Returns a list of every investment in the system.")
    @ApiResponse(responseCode = "200", description = "All investments retrieved")
    public ResponseEntity<List<InvestmentResponse>> getAllInvestment() {
        return ResponseEntity.ok().body(investmentService.listAllInvestments());
    }

    @PatchMapping(value = "/patch/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Partial update investment", description = "Updates specific fields of an investment record.")
    @ApiResponse(responseCode = "200", description = "Investment patched successfully")
    public ResponseEntity<InvestmentResponse> patchInvestment(@PathVariable int id, @RequestBody InvestmentPatchRequest patchRequest) {
        return ResponseEntity.ok().body(investmentService.patchInvestment(id, patchRequest));
    }

    @PutMapping("/activate/{id}")
    @Operation(summary = "Activate investment", description = "Reactivates an investment that was previously deactivated.")
    @ApiResponse(responseCode = "200", description = "Investment activated successfully")
    public ResponseEntity<String> activateInvestment(@PathVariable int id) {
        investmentService.activateInvestment(id);
        return ResponseEntity.ok("Investment activated with id: " + id);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete investment", description = "Performs a soft or hard delete of the investment record.")
    @ApiResponse(responseCode = "200", description = "Investment successfully deleted")
    public ResponseEntity<String> deleteInvestment(@PathVariable int id) {
        investmentService.deleteInvestment(id);
        return ResponseEntity.ok("Investment successfully deleted with id: " + id);
    }
}