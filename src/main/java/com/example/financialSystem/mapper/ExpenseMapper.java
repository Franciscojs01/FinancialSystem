package com.example.financialSystem.mapper;

import com.example.financialSystem.dto.ExpenseRequest;
import com.example.financialSystem.model.Expense;
import org.springframework.stereotype.Component;

@Component
public class ExpenseMapper {
    public Expense toEntity(ExpenseRequest dto) {
        Expense expense = new Expense();
        expense.setType(dto.expenseType());
        expense.setValue(dto.value());
        expense.setDateFinancial(dto.dateFinancial());
        expense.setBaseCurrency(dto.baseCurrency());
        expense.setPaymentMethod(dto.paymentMethod());
        expense.setFixed(dto.isFixed());

        return expense;
    }

}
