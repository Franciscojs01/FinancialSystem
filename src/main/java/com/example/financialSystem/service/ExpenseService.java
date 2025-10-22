package com.example.financialSystem.service;

import com.example.financialSystem.dto.ExpenseDto;
import com.example.financialSystem.exceptions.ExpenseDuplicateExcepetion;
import com.example.financialSystem.model.Expense;
import com.example.financialSystem.model.User;
import com.example.financialSystem.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ExpenseService extends UserLoggedService {
    @Autowired
    private ExpenseRepository expenseRepository;

    public ExpenseDto createExpense(Expense expense) {
        User user = getLoggedUser().getUser();
        expense.setUser(user);

        if (expense.getDateFinancial().isAfter(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("You cannot create an expense with an invalid date");
        }

        Optional<Expense> existingExpense = expenseRepository
                .findByUserAndTypeAndDateFinancialAndValueAndPaymentMethod(
                        user,
                        expense.getType(),
                        expense.getDateFinancial(),
                        expense.getValue(),
                        expense.getPaymentMethod()
                );

        if (existingExpense.isPresent()) {
            throw new ExpenseDuplicateExcepetion("Expense already exists");
        }

        expenseRepository.save(expense);
        return new ExpenseDto(expense);
    }


}
