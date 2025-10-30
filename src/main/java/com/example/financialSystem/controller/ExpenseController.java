package com.example.financialSystem.controller;


import com.example.financialSystem.dto.ExpenseResponse;
import com.example.financialSystem.dto.ExpenseRequest;
import com.example.financialSystem.mapper.ExpenseMapper;
import com.example.financialSystem.model.Expense;
import com.example.financialSystem.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/expense")
public class ExpenseController {
    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private ExpenseMapper expenseMapper;

    @PostMapping("/create")
    public ResponseEntity<ExpenseResponse> createExpense(@Valid @RequestBody ExpenseRequest expenseDto) {
        Expense expense = expenseMapper.toEntity(expenseDto);
        ExpenseResponse createdExpense = expenseService.createExpense(expense);
        return ResponseEntity.ok().body(createdExpense);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<ExpenseResponse> editExpense(@PathVariable int id, @Valid @RequestBody ExpenseRequest expensedto) {
        Expense expense = expenseMapper.toEntity(expensedto);
        ExpenseResponse updatedExpense = expenseService.editExpense(id, expense);

        return ResponseEntity.ok().body(updatedExpense);
    }
}
