package com.example.financialSystem.repository;

import com.example.financialSystem.model.entity.Expense;
import com.example.financialSystem.model.entity.User;
import com.example.financialSystem.model.enums.ExpenseType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface ExpenseRepository extends BaseRepository<Expense, Integer> {

    Optional<Expense> findById(int id);

    Optional<Expense> findByUserAndExpenseTypeAndDateFinancialAndValueAndPaymentMethod(User user, ExpenseType expenseType, LocalDate dateFinancial, BigDecimal value, String paymentMethod);

    List<Expense> findByUser(User user);

    void deleteById(int id);
}
