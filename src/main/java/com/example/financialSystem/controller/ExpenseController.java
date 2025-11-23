package com.example.financialSystem.controller;


import com.example.financialSystem.dto.requests.ExpensePatchRequest;
import com.example.financialSystem.dto.responses.ExpenseResponse;
import com.example.financialSystem.dto.requests.ExpenseRequest;
import com.example.financialSystem.mapper.ExpenseMapper;
import com.example.financialSystem.model.Expense;
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

    @Autowired
    private ExpenseMapper expenseMapper;

    @PostMapping("/create")
    public ResponseEntity<ExpenseResponse> createExpense(@Valid @RequestBody ExpenseRequest expenseRequest) {
        Expense expense = expenseMapper.toEntity(expenseRequest);
        ExpenseResponse createdExpense = expenseService.createExpense(expense);
        return ResponseEntity.ok().body(createdExpense);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<ExpenseResponse> editExpense(@PathVariable int id, @Valid @RequestBody ExpenseRequest expenseRequest) {
        Expense expense = expenseMapper.toEntity(expenseRequest);
        ExpenseResponse updatedExpense = expenseService.editExpense(id, expense);
        return ResponseEntity.ok().body(updatedExpense);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponse> getExpense(@PathVariable int id) {
        return ResponseEntity.ok().body(expenseService.getExpenseById(id));
    }

    @GetMapping("/list")
    public ResponseEntity<List<ExpenseResponse>> getAllExpense() {
        List<ExpenseResponse> expenses = expenseMapper.toDtoList(expenseService.listExpense());
        return ResponseEntity.ok().body(expenses);
    }

    @PatchMapping("/patch/{id}")
    public ResponseEntity<ExpenseResponse> patchExpense(@PathVariable int id, @Valid @RequestBody ExpensePatchRequest patchRequest) {
        ExpenseResponse updatedExpense = expenseService.patchExpense(id, patchRequest);
        return ResponseEntity.ok().body(updatedExpense);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExpense(@PathVariable int id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.ok("Expense success deleted ");
    }

}
