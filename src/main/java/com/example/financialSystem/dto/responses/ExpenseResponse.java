package com.example.financialSystem.dto.responses;

import com.example.financialSystem.model.Expense;
import com.example.financialSystem.model.enums.ExpenseType;
import com.example.financialSystem.util.BenchMarkRate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
public class ExpenseResponse {
    private int idExpense;
    private ExpenseType expenseType;
    private BigDecimal value;
    private BenchMarkRate baseCurrency;
    private LocalDate expenseDate;
    private String paymentMethod;
    private boolean isFixed;

    public ExpenseResponse(Expense entityExpense) {
        this.idExpense = entityExpense.getId();
        this.expenseType = entityExpense.getType();
        this.value = entityExpense.getValue();
        this.baseCurrency = entityExpense.getBaseCurrency();
        this.expenseDate = entityExpense.getDateFinancial();
        this.paymentMethod = entityExpense.getPaymentMethod();
        this.isFixed = entityExpense.isFixed();
    }
}
