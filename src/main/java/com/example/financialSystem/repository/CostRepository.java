package com.example.financialSystem.repository;

import com.example.financialSystem.model.entity.Cost;
import com.example.financialSystem.model.entity.User;
import com.example.financialSystem.model.enums.CostType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CostRepository extends BaseRepository<Cost, Integer> {

    boolean existsByUserAndCostTypeAndDateFinancial(User user, CostType type, LocalDate dateFinancial);

    Optional<Cost> findById(int id);

    List<Cost> findByUser(User user);

    void deleteById(int id);
}
