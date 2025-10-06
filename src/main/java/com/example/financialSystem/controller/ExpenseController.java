package com.example.financialSystem.controller;


import com.example.financialSystem.dto.ExpenseDto;
import com.example.financialSystem.model.Expense;
import com.example.financialSystem.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/expense")
public class ExpenseController {
    @Autowired
    private ExpenseService expenseService;

    @PostMapping("/create")
    public ResponseEntity<ExpenseDto> createExpense(@Valid @RequestBody Expense expense) {
        ExpenseDto expenseDto = expenseService.createExpense(expense);
        return ResponseEntity.ok().body(expenseDto);
    }
    

}
