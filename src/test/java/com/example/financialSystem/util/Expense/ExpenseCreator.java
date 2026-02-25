package com.example.financialSystem.util.Expense;

import com.example.financialSystem.models.entity.Expense;
import com.example.financialSystem.models.enums.ExpenseType;
import com.example.financialSystem.utils.BenchMarkRate;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseCreator {
    public static Expense createExpense() {
        return Expense.builder()
                .expenseType(ExpenseType.FOOD)
                .value(BigDecimal.valueOf(100.00))
                .baseCurrency(BenchMarkRate.EUR)
                .dateFinancial(LocalDate.of(2024, 6, 1))
                .paymentMethod("Credit Card")
                .isFixed(false)
                .build();
    }
}
