package com.example.financialSystem.service;

import com.example.financialSystem.dto.requests.ExpensePatchRequest;
import com.example.financialSystem.dto.requests.ExpenseRequest;
import com.example.financialSystem.dto.responses.ExpenseResponse;
import com.example.financialSystem.exception.ExpenseDuplicateException;
import com.example.financialSystem.exception.ExpenseNotFoundException;
import com.example.financialSystem.exception.NoChangeDetectedException;
import com.example.financialSystem.mapper.ExpenseMapper;
import com.example.financialSystem.model.Expense;
import com.example.financialSystem.model.Login;
import com.example.financialSystem.model.User;
import com.example.financialSystem.repository.ExpenseRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ExpenseService extends UserLoggedService {
    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private ExpenseMapper expenseMapper;

    public ExpenseResponse createExpense(ExpenseRequest request) {
        User user = getLoggedUser().getUser();
        Expense expense = expenseMapper.toEntity(request);

        validateExpenseDate(expense.getDateFinancial());

        expenseRepository
                .findByUserAndTypeAndDateFinancialAndValueAndPaymentMethod(
                        user,
                        expense.getType(),
                        expense.getDateFinancial(),
                        expense.getValue(),
                        expense.getPaymentMethod()
                ).ifPresent(existingExpense -> {
                    throw new ExpenseDuplicateException("Expense already exists");
                });

        expenseRepository.save(expense);
        return new ExpenseResponse(expense);
    }

    public ExpenseResponse editExpense(int id, ExpenseRequest updatedExpense) {
        Expense existingExpense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));

        validateOwnerShip(existingExpense);
        validateExpenseDate(existingExpense.getDateFinancial());

        ensureChanged(existingExpense, updatedExpense);

        existingExpense.setType(updatedExpense.expenseType());
        existingExpense.setValue(updatedExpense.value());
        existingExpense.setDateFinancial(updatedExpense.dateFinancial());
        existingExpense.setBaseCurrency(updatedExpense.baseCurrency());
        existingExpense.setPaymentMethod(updatedExpense.paymentMethod());
        existingExpense.setFixed(updatedExpense.isFixed());

        return new ExpenseResponse(expenseRepository.save(existingExpense));
    }

    public List<ExpenseResponse> listExpense() {
        return expenseMapper.toDtoList(expenseRepository.findByUser(getLoggedUser().getUser()));
    }

    public ExpenseResponse patchExpense(int id, ExpensePatchRequest patchRequest) {
        Expense existingExpense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));

        validateOwnerShip(existingExpense);

        validateExpenseDate(existingExpense.getDateFinancial());

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

        validateOwnerShip(expense);

        return new ExpenseResponse(expense);
    }

    @Transactional
    public void deleteExpense(int id) {
        Expense expense = expenseRepository.findById(id)
                        .orElseThrow(() -> new ExpenseNotFoundException(id));

        validateOwnerShip(expense);

        expenseRepository.deleteById(id);
    }

    public void ensureChanged(Expense existingExpense, ExpenseRequest updatedExpense) {
        boolean unchanged =
                existingExpense.getType().equals(updatedExpense.expenseType()) &&
                        existingExpense.getValue().compareTo(updatedExpense.value()) == 0 &&
                        existingExpense.getDateFinancial().equals(updatedExpense.dateFinancial()) &&
                        existingExpense.getBaseCurrency().equals(updatedExpense.baseCurrency()) &&
                        existingExpense.getPaymentMethod().equals(updatedExpense.paymentMethod()) &&
                        existingExpense.isFixed() == updatedExpense.isFixed();

        if (unchanged) {
            throw new NoChangeDetectedException("No changes detected in this expense");
        }

    }

    private void validateOwnerShip(Expense expense) {
        Login loggedInUser = getLoggedUser();

        if (expense.getUser().getId() != loggedInUser.getId()) {
            throw new AccessDeniedException("You are not authorized to view this expense");
        }
    }

    private void validateExpenseDate(LocalDate date) {
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("You cannot create an expense with an invalid date");
        }

    }

}
