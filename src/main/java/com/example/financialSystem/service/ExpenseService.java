package com.example.financialSystem.service;

import com.example.financialSystem.dto.ExpenseResponse;
import com.example.financialSystem.exceptions.ExpenseDuplicateExcepetion;
import com.example.financialSystem.exceptions.ExpenseNotFoundException;
import com.example.financialSystem.exceptions.NoChangeDetectedException;
import com.example.financialSystem.model.Expense;
import com.example.financialSystem.model.Login;
import com.example.financialSystem.model.User;
import com.example.financialSystem.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ExpenseService extends UserLoggedService {
    @Autowired
    private ExpenseRepository expenseRepository;

    public ExpenseResponse createExpense(Expense expense) {
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
        return new ExpenseResponse(expense);
    }

    public ExpenseResponse editExpense(int id, Expense updatedExpense) {
        Expense existingExpense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense with id: " + id + " not found"));

        validateOnwerShip(existingExpense);
        ensureChanged(existingExpense, updatedExpense);

        existingExpense.setType(updatedExpense.getType());
        existingExpense.setValue(updatedExpense.getValue());
        existingExpense.setDateFinancial(updatedExpense.getDateFinancial());
        existingExpense.setBaseCurrency(updatedExpense.getBaseCurrency());
        existingExpense.setPaymentMethod(updatedExpense.getPaymentMethod());
        existingExpense.setFixed(updatedExpense.isFixed());

        expenseRepository.save(updatedExpense);

        return new ExpenseResponse(updatedExpense);
    }

    public ExpenseResponse getExpenseById(int id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException("Investment with Id " + id + "Not found"));

        validateOnwerShip(expense);

        return new ExpenseResponse(expense);
    }

    public void ensureChanged(Expense existingExpense, Expense updatedExpense) {
        boolean unchanged =
                existingExpense.getType().equals(updatedExpense.getType()) &&
                        existingExpense.getValue().compareTo(updatedExpense.getValue()) == 0 &&
                        existingExpense.getDateFinancial().equals(updatedExpense.getDateFinancial()) &&
                        existingExpense.getBaseCurrency().equals(updatedExpense.getBaseCurrency()) &&
                        existingExpense.getPaymentMethod().equals(updatedExpense.getPaymentMethod()) &&
                        existingExpense.isFixed() == updatedExpense.isFixed();

        if (unchanged) {
            throw new NoChangeDetectedException("No changes detected in this expense");
        }

    }

    private void validateOnwerShip(Expense expense) {
        Login loggedInUser = getLoggedUser();

        if (expense.getUser().getId() != loggedInUser.getId()) {
            throw new AccessDeniedException("You are not authorized to view this expense");
        }
    }


}
