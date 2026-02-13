package com.example.financialSystem.models.dto.responses;

import com.example.financialSystem.models.enums.ExpenseType;
import com.example.financialSystem.models.enums.FinancialType;
import com.example.financialSystem.utils.BenchMarkRate;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ExpenseResponse {
    private int id;
    private ExpenseType expenseType;
    private FinancialType financialType;
    private BigDecimal value;
    private LocalDate dateFinancial;
    private BenchMarkRate baseCurrency;
    private String paymentMethod;

    @JsonProperty("isFixed")
    private boolean isFixed;
}
