package com.example.financialSystem.model.dto.requests;

import com.example.financialSystem.model.enums.ExpenseType;
import com.example.financialSystem.util.BenchMarkRate;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseRequest(
        @NotNull(message = "type is required")
        ExpenseType expenseType,

        BigDecimal value,
        LocalDate dateFinancial,
        BenchMarkRate baseCurrency,

        @NotNull(message = "paymentMethod are required")
        String paymentMethod,
        @NotNull(message = "this value is required")
        boolean isFixed
) {
}

