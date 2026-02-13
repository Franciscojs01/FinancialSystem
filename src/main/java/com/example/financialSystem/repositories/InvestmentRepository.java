package com.example.financialSystem.repositories;

import com.example.financialSystem.models.entity.Investment;
import com.example.financialSystem.models.enums.InvestmentType;
import com.example.financialSystem.models.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;

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
