package com.example.financialSystem.util.Expense;

import com.example.financialSystem.models.dto.requests.ExpensePatchRequest;
import com.example.financialSystem.models.dto.requests.ExpenseRequest;
import com.example.financialSystem.utils.BenchMarkRate;
import com.example.financialSystem.models.enums.ExpenseType;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseRequestCreator {

    public static ExpenseRequest createExpenseRequest() {
        return new ExpenseRequest(
                ExpenseType.FOOD,
                new BigDecimal("100.00"),
                LocalDate.of(2024, 6, 1),
                BenchMarkRate.BRL,
                "Credit Card",
                false
        );
    }

    public static ExpenseRequest createUpdatedExpenseRequest() {
        return new ExpenseRequest(
                ExpenseType.TRANSPORT,
                new BigDecimal("200.00"),
                LocalDate.of(2024, 1, 1),
                BenchMarkRate.USD,
                "Debit Card",
                true
        );
    }

    public static ExpensePatchRequest createExpensePatchRequest() {
        return new ExpensePatchRequest(
                ExpenseType.HEALTH,
                new BigDecimal("150.00"),
                null,
                null,
                null,
                false
        );
    }
}
