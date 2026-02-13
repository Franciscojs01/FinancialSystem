package com.example.financialSystem.repositories;

import com.example.financialSystem.models.entity.Expense;
import com.example.financialSystem.models.entity.User;
import com.example.financialSystem.models.enums.ExpenseType;
import org.springframework.data.jpa.repository.EntityGraph;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface ExpenseRepository extends BaseRepository<Expense, Integer> {

    @EntityGraph(attributePaths = {"user"})
    Optional<Expense> findById(int id);

    @EntityGraph(attributePaths = {"user"})
    Optional<Expense> findByUserAndExpenseTypeAndDateFinancialAndValueAndPaymentMethod(User user, ExpenseType expenseType, LocalDate dateFinancial, BigDecimal value, String paymentMethod);

    @EntityGraph(attributePaths = {"user"})
    List<Expense> findByUserAndDeletedFalse(User user);

    @EntityGraph(attributePaths = {"user"})
    List<Expense> findAllActive();

}
