package com.example.financialSystem.service;

import com.example.financialSystem.exception.ExpenseDuplicateException;
import com.example.financialSystem.exception.ExpenseNotFoundException;
import com.example.financialSystem.exception.NoChangeDetectedException;
import com.example.financialSystem.model.dto.requests.ExpensePatchRequest;
import com.example.financialSystem.model.dto.requests.ExpenseRequest;
import com.example.financialSystem.model.dto.responses.ExpenseResponse;
import com.example.financialSystem.model.entity.Expense;
import com.example.financialSystem.model.entity.Login;
import com.example.financialSystem.model.entity.User;
import com.example.financialSystem.model.enums.FinancialType;
import com.example.financialSystem.model.enums.UserRole;
import com.example.financialSystem.model.mapper.ExpenseMapper;
import com.example.financialSystem.repository.ExpenseRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
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
        expense.setFinancialType(FinancialType.EXPENSE);

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

    @Transactional
    public ExpenseResponse updateExpense(int id, ExpenseRequest request) {
        Expense existingExpense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));

        validateOwnerShip(existingExpense);
        validateExpenseDate(existingExpense.getDateFinancial());

        ensureChanged(existingExpense, request);

        expenseMapper.updateEntityFromUpdate(request, existingExpense);
        return expenseMapper.toResponse(expenseRepository.save(existingExpense));
    }

    @Transactional
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

    public List<ExpenseResponse> listExpense() {
        return expenseMapper.toResponseList(expenseRepository.findByUser(getLoggedUser().getUser()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<ExpenseResponse> listAllExpense() {
        return expenseMapper.toResponseList(expenseRepository.findAll());
    }

    @Transactional
    public void deleteExpense(int id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));

        validateOwnerShip(expense);

        expenseRepository.deleteById(id);
    }

    public void ensureChanged(Expense oldExpense, ExpenseRequest newExpReq) {
        boolean unchanged =
                oldExpense.getExpenseType() == (newExpReq.expenseType()) ||
                        oldExpense.getDateFinancial().equals(newExpReq.dateFinancial()) ||
                        oldExpense.getValue().compareTo(newExpReq.value()) == 0 ||
                        oldExpense.getBaseCurrency() == newExpReq.baseCurrency() ||
                        oldExpense.getPaymentMethod().equals(newExpReq.paymentMethod()) ||
                        oldExpense.isFixed() == newExpReq.isFixed();

        if (unchanged) {
            throw new NoChangeDetectedException("No changes in this expense");
        }
    }

    private void validateOwnerShip(Expense expense) {
        Login loggedUser = getLoggedUser();

        boolean isOwnerOrAdmin = loggedUser.getUser().getUserRole() == UserRole.ADMIN
                || loggedUser.getUser().getId() == expense.getUser().getId();

        if (!isOwnerOrAdmin) {
            throw new AccessDeniedException("You can only edit your own account");
        }
    }

    private void validateExpenseDate(LocalDate date) {
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("You cannot create an expense with an invalid date");
        }

    }

}
