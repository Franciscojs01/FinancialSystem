package com.example.financialSystem.repository;

import com.example.financialSystem.model.Expense;
import com.example.financialSystem.model.User;
import com.example.financialSystem.model.enums.ExpenseType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    Optional<Expense> findById(int id);

    Optional<Expense> findByUserAndTypeAndDateFinancialAndValueAndPaymentMethod(User user, ExpenseType type, LocalDate dateFinancial, BigDecimal value, String paymentMethod);

    List<Expense> findByUser(User user);

    void deleteById(int id);
}
