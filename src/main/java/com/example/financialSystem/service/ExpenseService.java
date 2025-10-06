package com.example.financialSystem.service;

import com.example.financialSystem.dto.ExpenseDto;
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

    public ExpenseDto createExpense(Expense newExpense) {
        User user = getLoggedUser().getUser();
        newExpense.setUser(user);

        Optional<Expense> existingExpense = expenseRepository
                .findByUserAndDateFinancialAndValueAndPaymentMethod(
                        user,
                        newExpense.getDateFinancial(),
                        newExpense.getValue(),
                        newExpense.getPaymentMethod()
                );

        if (existingExpense.isEmpty()) {
            Expense savedExpense = expenseRepository.save(newExpense);
            return new ExpenseDto(savedExpense);
        }


        return ;
    }



}
