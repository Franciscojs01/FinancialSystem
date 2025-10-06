package com.example.financialSystem.controller;


import com.example.financialSystem.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {
    @Autowired
    private ExpenseService expenseService;

    

}
