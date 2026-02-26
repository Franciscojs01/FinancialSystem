package com.example.financialSystem.repositories;

import com.example.financialSystem.models.entity.Expense;
import com.example.financialSystem.models.entity.User;
import com.example.financialSystem.models.enums.ExpenseType;
import com.example.financialSystem.util.Expense.ExpenseCreator;
import com.example.financialSystem.util.User.UserCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("test for Expense Repository")
class ExpenseRepositoryTest extends ExpenseCreator {
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Save Expense When is Valid")
    void save_PersistExpense_WhenIsValid() {
        Expense expenseToBeSaved = createExpense();
        Expense savedExpense = expenseRepository.save(expenseToBeSaved);

        Assertions.assertThat(savedExpense).isNotNull();
        Assertions.assertThat(savedExpense.getId()).isNotNull();
        Assertions.assertThat(savedExpense.getExpenseType()).isEqualTo(expenseToBeSaved.getExpenseType());
        Assertions.assertThat(savedExpense.getValue()).isEqualTo(expenseToBeSaved.getValue());
        Assertions.assertThat(savedExpense.getPaymentMethod()).isEqualTo(expenseToBeSaved.getPaymentMethod());
    }

    @Test
    @DisplayName("Update Expense When Successful")
    void update_UpdateExpense_WhenSuccessful() {
        Expense expenseToBeSaved = createExpense();
        Expense updateExpense = expenseRepository.save(expenseToBeSaved);

        updateExpense.setExpenseType(ExpenseType.OTHER);
        Expense updatedExpense = expenseRepository.save(updateExpense);

        Assertions.assertThat(updatedExpense).isNotNull();
        Assertions.assertThat(updatedExpense.getId()).isNotNull();
        Assertions.assertThat(updatedExpense.getExpenseType()).isEqualTo(ExpenseType.OTHER);
    }

    @Test
    @DisplayName("Find Expense By Id When Successful")
    void findById_ReturnExpense_WhenSuccessful() {
        Expense expenseToBeFound = createExpense();
        Expense savedExpense = expenseRepository.save(expenseToBeFound);

        Optional<Expense> foundExpense = expenseRepository.findById(savedExpense.getId());

        Assertions.assertThat(foundExpense).isPresent();
        Assertions.assertThat(foundExpense.get().getId()).isEqualTo(savedExpense.getId());
        Assertions.assertThat(foundExpense.get().getExpenseType()).isEqualTo(savedExpense.getExpenseType());
        Assertions.assertThat(foundExpense.get().getValue()).isEqualTo(savedExpense.getValue());
    }

    @Test
    @DisplayName("Find Expense By User And Multiple Parameters When Successful")
    void findByUserAndExpenseTypeAndDateFinancialAndValueAndPaymentMethod_ReturnExpense_WhenSuccessful() {
        User user = UserCreator.createUser();
        User savedUser = userRepository.save(user);

        Expense expense = createExpense();
        expense.setUser(savedUser);
        Expense savedExpense = expenseRepository.save(expense);

        Optional<Expense> foundExpense = expenseRepository.findByUserAndExpenseTypeAndDateFinancialAndValueAndPaymentMethod(
                savedUser,
                savedExpense.getExpenseType(),
                savedExpense.getDateFinancial(),
                savedExpense.getValue(),
                savedExpense.getPaymentMethod()
        );

        Assertions.assertThat(foundExpense).isPresent();
        Assertions.assertThat(foundExpense.get().getUser().getId()).isEqualTo(savedUser.getId());
        Assertions.assertThat(foundExpense.get().getExpenseType()).isEqualTo(savedExpense.getExpenseType());
    }

    @Test
    @DisplayName("List All Expenses When Successful")
    void findAllActive_ReturnListOfExpenses_WhenSuccessful() {
        User user = UserCreator.createUserAdmin();
        User savedUser = userRepository.save(user);

        Expense expense = createExpense();
        expense.setUser(savedUser);
        expenseRepository.save(expense);

        List<Expense> expenseList = expenseRepository.findAllActive();

        Assertions.assertThat(expenseList).isNotNull();
        Assertions.assertThat(expenseList).isNotEmpty();
    }

    @Test
    @DisplayName("List expenses of activated user When Successful")
    void findByUserAndDeletedFalse_ReturnListOfExpenses_WhenSuccessful() {
        User user = UserCreator.createUser();
        User savedUser = userRepository.save(user);

        Expense expense = createExpense();
        expense.setUser(savedUser);
        expenseRepository.save(expense);

        List<Expense> expenseList = expenseRepository.findByUserAndDeletedFalse(savedUser);

        Assertions.assertThat(expenseList).isNotNull();
        Assertions.assertThat(expenseList).hasSize(1);
        Assertions.assertThat(expenseList.get(0).getExpenseType()).isEqualTo(expense.getExpenseType());
        Assertions.assertThat(expenseList.get(0).getValue()).isEqualTo(expense.getValue());
        Assertions.assertThat(expenseList.get(0).getUser().getId()).isEqualTo(savedUser.getId());
    }

    @Test
    @DisplayName("Delete Expense When Successful")
    void delete_RemoveExpense_WhenSuccessful() {
        Expense expenseToBeDeleted = createExpense();

        Expense savedExpense = expenseRepository.save(expenseToBeDeleted);
        savedExpense.setDeleted(Boolean.TRUE);
        expenseRepository.save(savedExpense);

        Optional<Expense> optionalExpense = expenseRepository.findById(savedExpense.getId());

        Assertions.assertThat(optionalExpense).isNotEmpty();
        Assertions.assertThat(optionalExpense.get().getDeleted()).isTrue();
    }
}