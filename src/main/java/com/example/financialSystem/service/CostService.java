package com.example.financialSystem.service;

import com.example.financialSystem.dto.requests.CostRequest;
import com.example.financialSystem.dto.responses.CostResponse;
import com.example.financialSystem.exceptions.CostDuplicateException;
import com.example.financialSystem.exceptions.CostNotFoundException;
import com.example.financialSystem.exceptions.NoChangeDetectedException;
import com.example.financialSystem.mapper.CostMapper;
import com.example.financialSystem.model.Cost;
import com.example.financialSystem.model.Login;
import com.example.financialSystem.model.User;
import com.example.financialSystem.repository.CostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
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

    public CostResponse updateCost(int id, CostRequest request) {
        Cost cost = costRepository.findById(id)
                .orElseThrow(() -> new CostNotFoundException("Cost with " + id + " not found"));


        costMapper.toEntity(request);

        validateOwerShip(cost);
        validateCostDate(cost.getDateFinancial());

        ensureChanged(cost, request);

        cost.setType(request.costType());
        cost.setObservation(request.observation());
        cost.setBaseCurrency(request.baseCurrency());
        cost.setDateFinancial(request.dateFinancial());

        costRepository.save(cost);
        return new CostResponse(cost);
    }

    public CostResponse getCostById(int id) {
        Cost cost = costRepository.findById(id)
                .orElseThrow(() -> new CostNotFoundException("Cost with " + id + " not found"));

        validateOwerShip(cost);

        return new CostResponse(cost);
    }


    public void validateCostDate(LocalDate date) {
        if (date.isAfter(LocalDate.now())) throw new IllegalArgumentException("Cost date cannot be in the future");
    }

    public void ensureChanged(Cost existingCost, CostRequest updatedCost) {
        boolean unchanged =
                existingCost.getType().equals(updatedCost.costType()) &&
                existingCost.getObservation().equals(updatedCost.observation()) &&
                existingCost.getValue().compareTo(updatedCost.value()) == 0 &&
                existingCost.getDateFinancial().equals(updatedCost.dateFinancial()) &&
                existingCost.getBaseCurrency().equals(updatedCost.baseCurrency());

        if (unchanged) {
            throw new NoChangeDetectedException("No changes detected in this cost");
        }
    }

    private void validateOwerShip(Cost cost) {
       Login login = getLoggedUser();

        if (cost.getUser().getId() != login.getId()) {
            throw new AccessDeniedException("You are not authorized to view this cost");
        }
    }
}
