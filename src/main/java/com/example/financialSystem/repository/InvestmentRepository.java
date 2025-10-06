package com.example.financialSystem.repository;

import com.example.financialSystem.model.Investment;
import com.example.financialSystem.model.enums.InvestmentType;
import com.example.financialSystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvestmentRepository extends JpaRepository<Investment, Long> {
    Optional<Investment> findByUserAndTypeAndBrokerName(User user, InvestmentType type, String brokerName);
    Optional<Investment> findById(int id);

    List<Investment> findByUser(User user);

    void deleteById(int id);
}
