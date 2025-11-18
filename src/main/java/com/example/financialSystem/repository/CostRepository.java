package com.example.financialSystem.repository;

import com.example.financialSystem.model.Cost;
import com.example.financialSystem.model.User;
import com.example.financialSystem.model.enums.CostType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface CostRepository extends JpaRepository<Cost,Long> {

    boolean existsByUserAndTypeAndDateFinancial(User user, CostType type, LocalDate dateFinancial);
}
