package com.example.financialSystem.repository;

import com.example.financialSystem.dto.responses.CostResponse;
import com.example.financialSystem.model.Cost;
import com.example.financialSystem.model.User;
import com.example.financialSystem.model.enums.CostType;
import com.example.financialSystem.service.CostService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CostRepository extends JpaRepository<Cost,Long> {

    boolean existsByUserAndTypeAndDateFinancial(User user, CostType type, LocalDate dateFinancial);

    Optional<Cost> findById(int id);

    List<Cost> findByUser(User user);

    void deleteById(int id);
}
