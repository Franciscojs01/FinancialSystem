package com.example.financialSystem.dto;

import com.example.financialSystem.model.enums.ExpenseType;
import com.example.financialSystem.util.BenchMarkRate;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ExpensePatchRequest {
    private ExpenseType expenseType;
    private BigDecimal value;
    private LocalDate dateFinancial;
    private BenchMarkRate baseCurrency;
    private String paymentMethod;
    private Boolean isFixed;

    public ExpensePatchRequest(
            ExpenseType expenseType, BigDecimal value, LocalDate dateFinancial,
            BenchMarkRate baseCurrency, String paymentMethod, boolean isFixed) {
        this.expenseType = expenseType;
        this.value = value;
        this.dateFinancial = dateFinancial;
        this.baseCurrency = baseCurrency;
        this.paymentMethod = paymentMethod;
        this.isFixed = isFixed;
    }

}