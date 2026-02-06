package com.example.financialSystem.controller;


import com.example.financialSystem.model.dto.requests.ExpensePatchRequest;
import com.example.financialSystem.model.dto.requests.ExpenseRequest;
import com.example.financialSystem.model.dto.responses.ExpenseResponse;
import com.example.financialSystem.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expense")
public class ExpenseController {
    @Autowired
    private ExpenseService expenseService;

    @PostMapping("/create")
    public ResponseEntity<ExpenseResponse> createExpense(@Valid @RequestBody ExpenseRequest expenseRequest) {
        return ResponseEntity.ok().body(expenseService.createExpense(expenseRequest));
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<ExpenseResponse> editExpense(@PathVariable int id, @Valid @RequestBody ExpenseRequest expenseRequest) {
        return ResponseEntity.ok().body(expenseService.updateExpense(id, expenseRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponse> getExpense(@PathVariable int id) {
        return ResponseEntity.ok().body(expenseService.getExpenseById(id));
    }

    @GetMapping("/list/me")
    public ResponseEntity<List<ExpenseResponse>> getAllExpenseByUser() {
        return ResponseEntity.ok().body(expenseService.listExpense());
    }

    @GetMapping("list/all")
    public ResponseEntity<List<ExpenseResponse>> getAllExpense() {
        return ResponseEntity.ok().body(expenseService.listAllExpense());
    }

    @PatchMapping("/patch/{id}")
    public ResponseEntity<ExpenseResponse> patchExpense(@PathVariable int id, @Valid @RequestBody ExpensePatchRequest patchRequest) {
        return ResponseEntity.ok().body(expenseService.patchExpense(id, patchRequest));
    }

    @PutMapping("/activate/{id}")
    public ResponseEntity<String> activateExpense(@PathVariable int id) {
        expenseService.activateExpense(id);
        return ResponseEntity.ok("Expense activated with id: " + id);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteExpense(@PathVariable int id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.ok("Expense success deleted ");
    }

}
