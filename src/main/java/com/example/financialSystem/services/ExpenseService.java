package com.example.financialSystem.services;

import com.example.financialSystem.exceptions.NoChangeDetectedException;
import com.example.financialSystem.exceptions.duplicates.ExpenseDuplicateException;
import com.example.financialSystem.exceptions.notFound.ExpenseNotFoundException;
import com.example.financialSystem.models.dto.requests.ExpensePatchRequest;
import com.example.financialSystem.models.dto.requests.ExpenseRequest;
import com.example.financialSystem.models.dto.responses.ExpenseResponse;
import com.example.financialSystem.models.entity.Expense;
import com.example.financialSystem.models.entity.Login;
import com.example.financialSystem.models.entity.User;
import com.example.financialSystem.models.enums.UserRole;
import com.example.financialSystem.models.mapper.ExpenseMapper;
import com.example.financialSystem.repositories.ExpenseRepository;
import com.example.financialSystem.repositories.LoginRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ExpenseService extends UserLoggedService {
    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;

    public ExpenseService(LoginRepository loginRepository, ExpenseRepository expenseRepository, ExpenseMapper expenseMapper) {
        super(loginRepository);
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
    }

    @Transactional
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
        return expenseMapper.toResponseList(expenseRepository.findByUserAndDeletedFalse(getLoggedUser().getUser()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<ExpenseResponse> listAllExpense() {
        return expenseMapper.toResponseList(expenseRepository.findAllActive());
    }

    @Transactional
    public void activateExpense(int id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));

        validateOwnerShip(expense);

        if (Boolean.FALSE.equals(expense.getDeleted())) {
            throw new IllegalArgumentException("Expense is already active");
        }

        expense.setDeleted(Boolean.FALSE);
        expenseRepository.save(expense);
    }

    @Transactional
    public void deleteExpense(int id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));

        validateOwnerShip(expense);

        if (Boolean.TRUE.equals(expense.getDeleted())) {
            throw new IllegalStateException("Expense is already deleted");
        }

        expense.setDeleted(Boolean.TRUE);
        expenseRepository.save(expense);
    }

    public void ensureChanged(Expense oldExpense, ExpenseRequest newExpReq) {

        boolean unchanged =
                oldExpense.getExpenseType() == newExpReq.expenseType()
                        && oldExpense.getDateFinancial().equals(newExpReq.dateFinancial())
                        && oldExpense.getValue().compareTo(newExpReq.value()) == 0
                        && oldExpense.getBaseCurrency() == newExpReq.baseCurrency()
                        && oldExpense.getPaymentMethod().equals(newExpReq.paymentMethod())
                        && oldExpense.isFixed() == newExpReq.isFixed();

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
