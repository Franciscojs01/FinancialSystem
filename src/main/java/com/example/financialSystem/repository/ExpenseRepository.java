package com.example.financialSystem.repository;

import com.example.financialSystem.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ExpenseRepository extends JpaRepository<Expense, Long> {

}
