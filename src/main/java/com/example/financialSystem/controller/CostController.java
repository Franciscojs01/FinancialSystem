package com.example.financialSystem.controller;

import com.example.financialSystem.exception.handler.ExceptionDetails;
import com.example.financialSystem.model.dto.requests.CostPatchRequest;
import com.example.financialSystem.model.dto.requests.CostRequest;
import com.example.financialSystem.model.dto.responses.CostResponse;
import com.example.financialSystem.service.CostService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/cost")
@Tag(name = "Costs", description = "Endpoints for managing operational or fixed costs")
public class CostController {

    private final CostService costService;

    public CostController(CostService costService) {
        this.costService = costService;
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new cost", description = "Registers a new cost entry in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cost created successfully",
                    content = @Content(schema = @Schema(implementation = CostResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ExceptionDetails.class)))
    })
    public ResponseEntity<CostResponse> createCost(@Valid @RequestBody CostRequest costRequest) {
        return ResponseEntity.ok().body(costService.createCost(costRequest));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get cost by ID", description = "Retrieves details of a specific cost entry.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cost found"),
            @ApiResponse(responseCode = "404", description = "Cost not found",
                    content = @Content(schema = @Schema(implementation = ExceptionDetails.class)))
    })
    public ResponseEntity<CostResponse> getCost(@PathVariable int id) {
        return ResponseEntity.ok().body(costService.getCostById(id));
    }

    @PutMapping(value = "/edit/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update cost", description = "Updates all information for an existing cost.")
    @ApiResponse(responseCode = "200", description = "Cost updated successfully")
    public ResponseEntity<CostResponse> editCost(@PathVariable int id, @Valid @RequestBody CostRequest costRequest) {
        return ResponseEntity.ok().body(costService.updateCost(id, costRequest));
    }

    @GetMapping(value = "/list/me", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List my costs", description = "Returns a list of costs associated with the current user.")
    @ApiResponse(responseCode = "200", description = "User costs retrieved")
    public ResponseEntity<List<CostResponse>> getAllCostByUser() {
        return ResponseEntity.ok().body(costService.listCost());
    }

    @GetMapping(value = "/list/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List all costs (Admin)", description = "Returns all cost records in the system.")
    @ApiResponse(responseCode = "200", description = "All costs retrieved")
    public ResponseEntity<List<CostResponse>> getAllCost() {
        return ResponseEntity.ok().body(costService.listAllCost());
    }

    @PatchMapping(value = "/patch/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Partial update cost", description = "Updates specific attributes of a cost record.")
    @ApiResponse(responseCode = "200", description = "Cost patched successfully")
    public ResponseEntity<CostResponse> patchCost(@PathVariable int id, @Valid @RequestBody CostPatchRequest costRequest) {
        return ResponseEntity.ok().body(costService.patchCost(id, costRequest));
    }

    @PutMapping("/activate/{id}")
    @Operation(summary = "Activate cost", description = "Reactivates a cost entry that was previously disabled.")
    @ApiResponse(responseCode = "200", description = "Cost activated successfully")
    public ResponseEntity<String> activateCost(@PathVariable int id) {
        costService.activateCost(id);
        return ResponseEntity.ok().body("Cost activated with id: " + id);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete cost", description = "Removes or deactivates a cost entry from the system.")
    @ApiResponse(responseCode = "200", description = "Cost successfully deleted")
    public ResponseEntity<String> deleteCost(@PathVariable int id) {
        costService.deleteCost(id);
        return ResponseEntity.ok().body("Cost with id " + id + " successfully deleted");
    }
}