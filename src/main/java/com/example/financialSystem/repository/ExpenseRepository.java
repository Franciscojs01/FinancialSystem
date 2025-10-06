package com.example.financialSystem.repository;

import com.example.financialSystem.model.Expense;
import com.example.financialSystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;


public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    Optional<Expense> findByUserAndDateFinancialAndValueAndPaymentMethod(
            User user, LocalDate dateFinancial, BigDecimal value, String paymentMethod
    );
}
