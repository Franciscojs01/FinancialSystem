package com.example.financialSystem.repository;

import com.example.financialSystem.model.entity.Investment;
import com.example.financialSystem.model.enums.InvestmentType;
import com.example.financialSystem.model.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvestmentRepository extends BaseRepository<Investment, Integer> {
    @EntityGraph(attributePaths = {"user"})
    Optional<Investment> findByUserAndInvestmentTypeAndBrokerName(User user, InvestmentType investmentType, String brokerName);

    @EntityGraph(attributePaths = {"user"})
    Optional<Investment> findById(int id);

    @EntityGraph(attributePaths = {"user"})
    List<Investment> findByUserAndDeletedFalse(User user);

    @EntityGraph(attributePaths = {"user"})
    List<Investment> findAllActive();

}
