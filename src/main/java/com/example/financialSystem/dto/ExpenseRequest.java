package com.example.financialSystem.dto;

import com.example.financialSystem.model.enums.ExpenseType;
import com.example.financialSystem.util.BenchMarkRate;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseRequest(
         ExpenseType expenseType,
         BigDecimal value,
         LocalDate dateFinancial,
         BenchMarkRate baseCurrency,
         String paymentMethod,
         boolean isFixed
){}

