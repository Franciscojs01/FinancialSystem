package com.example.financialSystem.service;

import com.example.financialSystem.model.dto.requests.ExpensePatchRequest;
import com.example.financialSystem.model.dto.requests.ExpenseRequest;
import com.example.financialSystem.model.dto.responses.ExpenseResponse;
import com.example.financialSystem.exception.ExpenseDuplicateException;
import com.example.financialSystem.exception.ExpenseNotFoundException;
import com.example.financialSystem.exception.NoChangeDetectedException;
import com.example.financialSystem.model.mapper.ExpenseMapper;
import com.example.financialSystem.model.entity.Expense;
import com.example.financialSystem.model.entity.Login;
import com.example.financialSystem.model.entity.User;
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
        expense.setUser(user);

        validateExpenseDate(expense.getDateFinancial());

        expenseRepository
                .findByUserAndExpenseTypeAndDateFinancialAndValueAndPaymentMethod(
                        user,
                        expense.getExpenseType(),
                        expense.getDateFinancial(),
                        expense.getValue(),
                        expense.getPaymentMethod()
                ).ifPresent(existingExpense -> {
                    throw new ExpenseDuplicateException("Expense already exists");
                });


        return expenseMapper.toResponse(expenseRepository.save(expense));
    }

    public ExpenseResponse editExpense(int id, ExpenseRequest request) {
        Expense existingExpense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));

        validateOwnerShip(existingExpense);
        validateExpenseDate(existingExpense.getDateFinancial());
        ensureChanged(existingExpense, request);

        expenseMapper.updateEntityFromUpdate(request, existingExpense);

        return expenseMapper.toResponse(expenseRepository.save(existingExpense));
    }

    public List<ExpenseResponse> listExpense() {
        return expenseMapper.toResponseList(expenseRepository.findByUser(getLoggedUser().getUser()));
    }

    public ExpenseResponse patchExpense(int id, ExpensePatchRequest patchRequest) {
        Expense existingExpense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));

        validateOwnerShip(existingExpense);
        validateExpenseDate(existingExpense.getDateFinancial());

        expenseMapper.updateEntityFromPatch(patchRequest, existingExpense);

        return expenseMapper.toResponse(expenseRepository.save(existingExpense));
    }

    public ExpenseResponse getExpenseById(int id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));

        validateOwnerShip(expense);

        return expenseMapper.toResponse(expense);
    }

    @Transactional
    public void deleteExpense(int id) {
        Expense expense = expenseRepository.findById(id)
                        .orElseThrow(() -> new ExpenseNotFoundException(id));

        validateOwnerShip(expense);

        expenseRepository.deleteById(id);
    }

    public void ensureChanged(Expense oldExpense, ExpenseRequest newExpReq) {
        ExpenseRequest oldAsRequest = expenseMapper.toRequest(oldExpense);

        if (oldAsRequest.equals(newExpReq)) throw new NoChangeDetectedException("No changes in this expense");

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
