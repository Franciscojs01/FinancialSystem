package com.example.financialSystem.model.dto.requests;

import com.example.financialSystem.model.enums.ExpenseType;
import com.example.financialSystem.util.BenchMarkRate;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpensePatchRequest {
    @Schema(description = "Name of the expense type", example = "FOOD, TRANSPORT, HOUSING, HEALTH, LEISURE, OTHER")
    private ExpenseType expenseType;
    @Schema(description = "value of the expense", example = "100.00")
    private BigDecimal value;
    @Schema(description = "date of the financial registry", example = "2024-06-01")
    private LocalDate dateFinancial;
    @Schema(description = "base currency for the expense", example = "USD, EUR, BRL")
    private BenchMarkRate baseCurrency;
    @Schema(description = "payment method for the expense", example = "CREDIT_CARD, DEBIT_CARD, CASH, TRANSFER")
    private String paymentMethod;
    @JsonProperty("isFixed")
    private boolean isFixed;
}