package com.example.financialSystem.controller;

import com.example.financialSystem.exception.handler.ExceptionDetails;
import com.example.financialSystem.model.dto.requests.ExpensePatchRequest;
import com.example.financialSystem.model.dto.requests.ExpenseRequest;
import com.example.financialSystem.model.dto.responses.ExpenseResponse;
import com.example.financialSystem.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expense")
@Tag(name = "Expenses", description = "Endpoints for managing financial expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping(value = "/create")
    @Operation(summary = "Create a new expense", description = "Registers a new expense in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense created successfully",
                    content = @Content(schema = @Schema(implementation = ExpenseResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ExceptionDetails.class)))
    })
    public ResponseEntity<ExpenseResponse> createExpense(@Valid @RequestBody ExpenseRequest expenseRequest) {
        return ResponseEntity.ok().body(expenseService.createExpense(expenseRequest));
    }

    @PutMapping(value = "/edit/{id}")
    @Operation(summary = "Update expense", description = "Updates all fields of an existing expense.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense updated successfully"),
            @ApiResponse(responseCode = "404", description = "Expense not found",
                    content = @Content(schema = @Schema(implementation = ExceptionDetails.class)))
    })
    public ResponseEntity<ExpenseResponse> editExpense(@PathVariable int id, @Valid @RequestBody ExpenseRequest expenseRequest) {
        return ResponseEntity.ok().body(expenseService.updateExpense(id, expenseRequest));
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Get expense by ID", description = "Returns the details of a specific expense.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense found"),
            @ApiResponse(responseCode = "404", description = "Expense not found",
                    content = @Content(schema = @Schema(implementation = ExceptionDetails.class)))
    })
    public ResponseEntity<ExpenseResponse> getExpense(@PathVariable int id) {
        return ResponseEntity.ok().body(expenseService.getExpenseById(id));
    }

    @GetMapping(value = "/list/me")
    @Operation(summary = "List my expenses", description = "Returns a list of expenses belonging to the authenticated user.")
    @ApiResponse(responseCode = "200", description = "List of user expenses retrieved")
    public ResponseEntity<List<ExpenseResponse>> getAllExpenseByUser() {
        return ResponseEntity.ok().body(expenseService.listExpense());
    }

    @GetMapping(value = "/list/all")
    @Operation(summary = "List all expenses (Admin)", description = "Returns all expenses in the system. Requires administrative privileges.")
    @ApiResponse(responseCode = "200", description = "List of all expenses retrieved")
    public ResponseEntity<List<ExpenseResponse>> getAllExpense() {
        return ResponseEntity.ok().body(expenseService.listAllExpense());
    }

    @PatchMapping(value = "/patch/{id}")
    @Operation(summary = "Partial update expense", description = "Updates specific fields of an expense.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense patched successfully"),
            @ApiResponse(responseCode = "404", description = "Expense not found")
    })
    public ResponseEntity<ExpenseResponse> patchExpense(@PathVariable int id, @Valid @RequestBody ExpensePatchRequest patchRequest) {
        return ResponseEntity.ok().body(expenseService.patchExpense(id, patchRequest));
    }

    @PutMapping("/activate/{id}")
    @Operation(summary = "Activate expense", description = "Reactivates a previously deleted or disabled expense.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense activated successfully"),
            @ApiResponse(responseCode = "404", description = "Expense not found")
    })
    public ResponseEntity<String> activateExpense(@PathVariable int id) {
        expenseService.activateExpense(id);
        return ResponseEntity.ok("Expense activated with id: " + id);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete expense", description = "Performs a soft or hard delete of the specified expense.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Expense not found")
    })
    public ResponseEntity<String> deleteExpense(@PathVariable int id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.ok("Expense successfully deleted");
    }
}