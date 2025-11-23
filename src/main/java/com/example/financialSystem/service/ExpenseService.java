package com.example.financialSystem.service;

import com.example.financialSystem.dto.requests.ExpensePatchRequest;
import com.example.financialSystem.dto.responses.ExpenseResponse;
import com.example.financialSystem.exception.ExpenseDuplicateException;
import com.example.financialSystem.exception.ExpenseNotFoundException;
import com.example.financialSystem.exception.NoChangeDetectedException;
import com.example.financialSystem.model.Expense;
import com.example.financialSystem.model.Login;
import com.example.financialSystem.model.User;
import com.example.financialSystem.repository.ExpenseRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService extends UserLoggedService {
    @Autowired
    private ExpenseRepository expenseRepository;

    public ExpenseResponse createExpense(Expense expense) {
        User user = getLoggedUser().getUser();
        expense.setUser(user);

        validateExpenseDate(expense);

        Optional<Expense> existingExpense = expenseRepository
                .findByUserAndTypeAndDateFinancialAndValueAndPaymentMethod(
                        user,
                        expense.getType(),
                        expense.getDateFinancial(),
                        expense.getValue(),
                        expense.getPaymentMethod()
                );

        if (existingExpense.isPresent()) {
            throw new ExpenseDuplicateException("Expense already exists");
        }

        expenseRepository.save(expense);
        return new ExpenseResponse(expense);
    }

    public ExpenseResponse editExpense(int id, Expense updatedExpense) {
        Expense existingExpense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));

        validateOnwerShip(existingExpense);

        validateExpenseDate(updatedExpense);

        ensureChanged(existingExpense, updatedExpense);

        existingExpense.setType(updatedExpense.getType());
        existingExpense.setValue(updatedExpense.getValue());
        existingExpense.setDateFinancial(updatedExpense.getDateFinancial());
        existingExpense.setBaseCurrency(updatedExpense.getBaseCurrency());
        existingExpense.setPaymentMethod(updatedExpense.getPaymentMethod());
        existingExpense.setFixed(updatedExpense.isFixed());

        updatedExpense = expenseRepository.save(existingExpense);

        return new ExpenseResponse(updatedExpense);
    }

    public List<Expense> listExpense() {
        return expenseRepository.findByUser(getLoggedUser().getUser());
    }

    public ExpenseResponse patchExpense(int id, ExpensePatchRequest patchRequest) {
        Expense existingExpense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));

        validateOnwerShip(existingExpense);

        validateExpenseDate(existingExpense);

        if (patchRequest.getValue() != null) {
            existingExpense.setValue(patchRequest.getValue());
        }

        if (patchRequest.getDateFinancial() != null) {
            existingExpense.setDateFinancial(patchRequest.getDateFinancial());
        }

        if (patchRequest.getPaymentMethod() != null) {
            existingExpense.setPaymentMethod(patchRequest.getPaymentMethod());
        }

        if (patchRequest.getIsFixed() != null) {
            existingExpense.setFixed(patchRequest.getIsFixed());
        }

        if (patchRequest.getBaseCurrency() != null)
            existingExpense.setBaseCurrency(patchRequest.getBaseCurrency());

        if (patchRequest.getExpenseType() != null) {
            existingExpense.setType(patchRequest.getExpenseType());
        }

        expenseRepository.save(existingExpense);

        return new ExpenseResponse(existingExpense);
    }

    public ExpenseResponse getExpenseById(int id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));

        validateOnwerShip(expense);

        return new ExpenseResponse(expense);
    }

    @Transactional
    public void deleteExpense(int id) {
        Expense expense = expenseRepository.findById(id)
                        .orElseThrow(() -> new ExpenseNotFoundException(id));

        validateOnwerShip(expense);

        expenseRepository.deleteById(id);
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

    private void validateExpenseDate(Expense expense) {
        if (expense.getDateFinancial().isAfter(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("You cannot create an expense with an invalid date");
        }

    }

}
