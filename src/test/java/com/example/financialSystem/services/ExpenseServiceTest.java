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
import com.example.financialSystem.models.enums.ExpenseType;
import com.example.financialSystem.models.enums.UserRole;
import com.example.financialSystem.models.mapper.ExpenseMapper;
import com.example.financialSystem.repositories.ExpenseRepository;
import com.example.financialSystem.repositories.LoginRepository;
import com.example.financialSystem.util.Expense.ExpenseCreator;
import com.example.financialSystem.util.Expense.ExpenseRequestCreator;
import com.example.financialSystem.util.User.UserCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for ExpenseService")
class ExpenseServiceTest {

    @InjectMocks
    private ExpenseService expenseService;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private ExpenseMapper expenseMapper;

    @Mock
    private LoginRepository loginRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private User user;
    private Login login;
    private Expense expense;
    private ExpenseResponse expenseResponse;

    @BeforeEach
    void setUp() {
        user = UserCreator.createUser();
        user.setId(1);
        user.setUserRole(UserRole.USER);

        login = new Login();
        login.setId(1);
        login.setUsername("test@test.com");
        login.setUser(user);

        expense = ExpenseCreator.createExpense();
        expense.setUser(user);

        expenseResponse = new ExpenseResponse();
        expenseResponse.setId(1);
        expenseResponse.setExpenseType(ExpenseType.FOOD);

        SecurityContextHolder.setContext(securityContext);
    }

    private void mockLoggedUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@test.com");
        when(loginRepository.findByUsername("test@test.com")).thenReturn(Optional.of(login));
    }

    @Test
    @DisplayName("createExpense returns ExpenseResponse when successful")
    void createExpense_ReturnsExpenseResponse_WhenSuccessful() {
        mockLoggedUser();
        ExpenseRequest request = ExpenseRequestCreator.createExpenseRequest();

        when(expenseMapper.toEntity(request)).thenReturn(expense);
        when(expenseRepository.findByUserAndExpenseTypeAndDateFinancialAndValueAndPaymentMethod(
                any(), any(), any(), any(), any())).thenReturn(Optional.empty());
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);
        when(expenseMapper.toResponse(expense)).thenReturn(expenseResponse);

        ExpenseResponse result = expenseService.createExpense(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    @DisplayName("createExpense throws ExpenseDuplicateException when expense already exists")
    void createExpense_ThrowsExpenseDuplicateException_WhenExpenseExists() {
        mockLoggedUser();
        ExpenseRequest request = ExpenseRequestCreator.createExpenseRequest();

        when(expenseMapper.toEntity(request)).thenReturn(expense);
        when(expenseRepository.findByUserAndExpenseTypeAndDateFinancialAndValueAndPaymentMethod(
                any(), any(), any(), any(), any())).thenReturn(Optional.of(expense));

        assertThatThrownBy(() -> expenseService.createExpense(request))
                .isInstanceOf(ExpenseDuplicateException.class);
    }

    @Test
    @DisplayName("createExpense throws IllegalArgumentException when date is in the future")
    void createExpense_ThrowsIllegalArgumentException_WhenDateIsInFuture() {
        mockLoggedUser();
        ExpenseRequest request = ExpenseRequestCreator.createExpenseRequest();

        Expense futureExpense = ExpenseCreator.createExpense();
        futureExpense.setDateFinancial(LocalDate.now().plusDays(1));

        when(expenseMapper.toEntity(request)).thenReturn(futureExpense);

        assertThatThrownBy(() -> expenseService.createExpense(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("invalid date");
    }

    @Test
    @DisplayName("updateExpense returns ExpenseResponse when successful")
    void updateExpense_ReturnsExpenseResponse_WhenSuccessful() {
        mockLoggedUser();
        ExpenseRequest request = ExpenseRequestCreator.createUpdatedExpenseRequest();

        when(expenseRepository.findById(1)).thenReturn(Optional.of(expense));
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);
        when(expenseMapper.toResponse(expense)).thenReturn(expenseResponse);

        ExpenseResponse result = expenseService.updateExpense(1, request);

        assertThat(result).isNotNull();
        verify(expenseMapper, times(1)).updateEntityFromUpdate(request, expense);
    }

    @Test
    @DisplayName("updateExpense throws ExpenseNotFoundException when expense not found")
    void updateExpense_ThrowsExpenseNotFoundException_WhenNotFound() {
        ExpenseRequest request = ExpenseRequestCreator.createUpdatedExpenseRequest();

        when(expenseRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> expenseService.updateExpense(999, request))
                .isInstanceOf(ExpenseNotFoundException.class);
    }

    @Test
    @DisplayName("updateExpense throws AccessDeniedException when user is not owner")
    void updateExpense_ThrowsAccessDeniedException_WhenUserIsNotOwner() {
        mockLoggedUser();
        ExpenseRequest request = ExpenseRequestCreator.createUpdatedExpenseRequest();

        User anotherUser = UserCreator.createUser();
        anotherUser.setId(999);
        expense.setUser(anotherUser);

        when(expenseRepository.findById(1)).thenReturn(Optional.of(expense));

        assertThatThrownBy(() -> expenseService.updateExpense(1, request))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("patchExpense returns ExpenseResponse when successful")
    void patchExpense_ReturnsExpenseResponse_WhenSuccessful() {
        mockLoggedUser();
        ExpensePatchRequest patchRequest = ExpenseRequestCreator.createExpensePatchRequest();

        when(expenseRepository.findById(1)).thenReturn(Optional.of(expense));
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);
        when(expenseMapper.toResponse(expense)).thenReturn(expenseResponse);

        ExpenseResponse result = expenseService.patchExpense(1, patchRequest);

        assertThat(result).isNotNull();
        verify(expenseMapper, times(1)).updateEntityFromPatch(patchRequest, expense);
    }

    @Test
    @DisplayName("getExpenseById returns ExpenseResponse when successful")
    void getExpenseById_ReturnsExpenseResponse_WhenSuccessful() {
        mockLoggedUser();

        when(expenseRepository.findById(1)).thenReturn(Optional.of(expense));
        when(expenseMapper.toResponse(expense)).thenReturn(expenseResponse);

        ExpenseResponse result = expenseService.getExpenseById(1);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
    }

    @Test
    @DisplayName("getExpenseById throws ExpenseNotFoundException when not found")
    void getExpenseById_ThrowsExpenseNotFoundException_WhenNotFound() {
        when(expenseRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> expenseService.getExpenseById(999))
                .isInstanceOf(ExpenseNotFoundException.class);
    }

    @Test
    @DisplayName("listExpense returns list of ExpenseResponse when successful")
    void listExpense_ReturnsListOfExpenseResponse_WhenSuccessful() {
        mockLoggedUser();

        when(expenseRepository.findByUserAndDeletedFalse(user)).thenReturn(List.of(expense));
        when(expenseMapper.toResponseList(List.of(expense))).thenReturn(List.of(expenseResponse));

        List<ExpenseResponse> result = expenseService.listExpense();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("listAllExpense returns list of all ExpenseResponse when successful")
    void listAllExpense_ReturnsListOfAllExpenseResponse_WhenSuccessful() {
        when(expenseRepository.findAllActive()).thenReturn(List.of(expense));
        when(expenseMapper.toResponseList(List.of(expense))).thenReturn(List.of(expenseResponse));

        List<ExpenseResponse> result = expenseService.listAllExpense();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("activateExpense activates expense when successful")
    void activateExpense_ActivatesExpense_WhenSuccessful() {
        mockLoggedUser();
        expense.setDeleted(true);

        when(expenseRepository.findById(1)).thenReturn(Optional.of(expense));
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);

        assertThatCode(() -> expenseService.activateExpense(1))
                .doesNotThrowAnyException();

        verify(expenseRepository, times(1)).save(expense);
    }

    @Test
    @DisplayName("activateExpense throws IllegalArgumentException when expense is already active")
    void activateExpense_ThrowsIllegalArgumentException_WhenAlreadyActive() {
        mockLoggedUser();
        expense.setDeleted(false);

        when(expenseRepository.findById(1)).thenReturn(Optional.of(expense));

        assertThatThrownBy(() -> expenseService.activateExpense(1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already active");
    }

    @Test
    @DisplayName("deleteExpense deletes expense when successful")
    void deleteExpense_DeletesExpense_WhenSuccessful() {
        mockLoggedUser();
        expense.setDeleted(false);

        when(expenseRepository.findById(1)).thenReturn(Optional.of(expense));
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);

        assertThatCode(() -> expenseService.deleteExpense(1))
                .doesNotThrowAnyException();

        verify(expenseRepository, times(1)).save(expense);
    }

    @Test
    @DisplayName("deleteExpense throws IllegalStateException when expense is already deleted")
    void deleteExpense_ThrowsIllegalStateException_WhenAlreadyDeleted() {
        mockLoggedUser();
        expense.setDeleted(true);

        when(expenseRepository.findById(1)).thenReturn(Optional.of(expense));

        assertThatThrownBy(() -> expenseService.deleteExpense(1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already deleted");
    }

    @Test
    @DisplayName("ensureChanged throws NoChangeDetectedException when no changes")
    void ensureChanged_ThrowsNoChangeDetectedException_WhenNoChanges() {
        ExpenseRequest sameRequest = new ExpenseRequest(
                expense.getExpenseType(),
                expense.getValue(),
                expense.getDateFinancial(),
                expense.getBaseCurrency(),
                expense.getPaymentMethod(),
                expense.isFixed()
        );

        assertThatThrownBy(() -> expenseService.ensureChanged(expense, sameRequest))
                .isInstanceOf(NoChangeDetectedException.class);
    }
}

