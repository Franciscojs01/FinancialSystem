package com.example.financialSystem.mapper;

import com.example.financialSystem.dto.requests.ExpenseRequest;
import com.example.financialSystem.dto.responses.ExpenseResponse;
import com.example.financialSystem.model.Expense;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public List<ExpenseResponse> toDtoList(List<Expense> expenses) {
        return expenses.stream()
                .map(ExpenseResponse::new)
                .toList();
    }

}
