package com.example.financialSystem.model.dto.requests;

import com.example.financialSystem.model.enums.ExpenseType;
import com.example.financialSystem.util.BenchMarkRate;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseRequest(
        @Schema(description = "Name of the expense type", example = "FOOD, TRANSPORT, HOUSING, HEALTH, LEISURE, OTHER")
        @NotNull(message = "type is required")
        ExpenseType expenseType,

        @Schema(description = "value of the expense", example = "100.00")
        @NotNull(message = "value is required")
        BigDecimal value,

        @Schema(description = "date of the financial registry", example = "2024-06-01")
        @NotNull(message = "dateFinancial is required")
        LocalDate dateFinancial,

        @Schema(description = "base currency for the expense", example = "USD, EUR, BRL")
        @NotNull(message = "baseCurrency is required")
        BenchMarkRate baseCurrency,

        @Schema(description = "payment method for the expense", example = "CREDIT_CARD, DEBIT_CARD, CASH, TRANSFER")
        @NotBlank(message = "paymentMethod are required")
        String paymentMethod,

        @NotNull(message = "this value is required")
        @JsonProperty("isFixed")
        boolean isFixed
) {
}

