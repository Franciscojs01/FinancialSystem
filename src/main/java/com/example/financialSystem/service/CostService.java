package com.example.financialSystem.service;

import com.example.financialSystem.dto.requests.CostRequest;
import com.example.financialSystem.dto.responses.CostResponse;
import com.example.financialSystem.exceptions.CostDuplicateException;
import com.example.financialSystem.exceptions.CostNotFoundException;
import com.example.financialSystem.mapper.CostMapper;
import com.example.financialSystem.model.Cost;
import com.example.financialSystem.model.User;
import com.example.financialSystem.repository.CostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class CostService extends UserLoggedService {
    @Autowired
    CostRepository costRepository;

    @Autowired
    private CostMapper costMapper;

    public CostResponse createCost(CostRequest request) {
        User user = getLoggedUser().getUser();

        Cost cost = costMapper.toEntity(request);
        cost.setUser(user);

        validateCostDate(cost.getDateFinancial());

        boolean exist = costRepository
                .existsByUserAndTypeAndDateFinancial(user, cost.getType(), cost.getDateFinancial());

        if (exist) {
            throw new CostDuplicateException("You already created a cost of this type on this date");
        }

        costRepository.save(cost);
        return new CostResponse(cost);
    }

    public void validateCostDate(LocalDate date) {
        if (date.isAfter(LocalDate.now())) throw new IllegalArgumentException("Cost date cannot be in the future");
    }

}
