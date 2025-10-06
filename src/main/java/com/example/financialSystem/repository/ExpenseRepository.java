package com.example.financialSystem.repository;

import com.example.financialSystem.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    Optional<Expense> findById(int id);
}
