package com.example.financialSystem.repositories;

import com.example.financialSystem.models.entity.Cost;
import com.example.financialSystem.models.entity.User;
import com.example.financialSystem.models.enums.CostType;
import org.springframework.data.jpa.repository.EntityGraph;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CostRepository extends BaseRepository<Cost, Integer> {

    boolean existsByUserAndCostTypeAndDateFinancial(User user, CostType type, LocalDate dateFinancial);

    @EntityGraph(attributePaths = {"user"})
    Optional<Cost> findById(int id);

    @EntityGraph(attributePaths = {"user"})
    List<Cost> findByUserAndDeletedFalse(User user);

    @EntityGraph(attributePaths = {"user"})
    List<Cost> findAllActive();

}
