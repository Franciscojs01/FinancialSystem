package com.example.financialSystem.model.dto.responses;

import com.example.financialSystem.model.enums.ExpenseType;
import com.example.financialSystem.util.BenchMarkRate;
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
    private BigDecimal value;
    private LocalDate dateFinancial;
    private BenchMarkRate baseCurrency;
    private String paymentMethod;
    private boolean isFixed;
}
