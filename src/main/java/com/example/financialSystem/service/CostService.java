package com.example.financialSystem.service;

import com.example.financialSystem.exception.CostDuplicateException;
import com.example.financialSystem.exception.CostNotFoundException;
import com.example.financialSystem.exception.NoChangeDetectedException;
import com.example.financialSystem.model.dto.requests.CostPatchRequest;
import com.example.financialSystem.model.dto.requests.CostRequest;
import com.example.financialSystem.model.dto.responses.CostResponse;
import com.example.financialSystem.model.entity.Cost;
import com.example.financialSystem.model.entity.Login;
import com.example.financialSystem.model.entity.User;
import com.example.financialSystem.model.enums.UserRole;
import com.example.financialSystem.model.mapper.CostMapper;
import com.example.financialSystem.repository.CostRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

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
                .existsByUserAndCostTypeAndDateFinancial(user, cost.getCostType(), cost.getDateFinancial());

        if (exist) {
            throw new CostDuplicateException("You already created a cost of this type on this date");
        }

        return costMapper.toResponse(costRepository.save(cost));
    }

    public CostResponse updateCost(int id, CostRequest request) {
        Cost cost = costRepository.findById(id)
                .orElseThrow(() -> new CostNotFoundException(id));

        costMapper.toEntity(request);

        validateOwerShip(cost);
        validateCostDate(cost.getDateFinancial());

        ensureChanged(cost, request);

        costMapper.updateFromUpdate(request, cost);

        return costMapper.toResponse(costRepository.save(cost));
    }

    public CostResponse getCostById(int id) {
        Cost cost = costRepository.findById(id)
                .orElseThrow(() -> new CostNotFoundException(id));

        validateOwerShip(cost);

        return costMapper.toResponse(cost);
    }

    public CostResponse patchCost(int id, CostPatchRequest patchRequest) {
        Cost existingCost = costRepository.findById(id)
                .orElseThrow(() -> new CostNotFoundException(id));

        validateOwerShip(existingCost);

        costMapper.updateFromPatch(patchRequest, existingCost);

        validateCostDate(existingCost.getDateFinancial());

        return costMapper.toResponse(costRepository.save(existingCost));
    }

    public List<CostResponse> listCost() {
        return costMapper.toResponseList(costRepository.findByUser(getLoggedUser().getUser()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<CostResponse> listAllCost() {
        return costMapper.toResponseList(costRepository.findAll());
    }

    @Transactional
    public void deleteCost(int id) {
        Cost cost = costRepository.findById(id)
                .orElseThrow(() -> new CostNotFoundException(id));

        validateOwerShip(cost);

        costRepository.deleteById(id);
    }

    public void validateCostDate(LocalDate date) {
        if (date.isAfter(LocalDate.now())) throw new IllegalArgumentException("Cost date cannot be in the future");
    }

    public void ensureChanged(Cost oldCost, CostRequest newCostReq) {
        CostRequest oldAsRequest = costMapper.toRequest(oldCost);
        if (oldAsRequest.equals(newCostReq)) throw new  NoChangeDetectedException("No change in this cost");
    }

    private void validateOwerShip(Cost cost) {
       Login loggedUser = getLoggedUser();

       boolean isOwnerOrAdmin = cost.getUser().getId() == loggedUser.getId()
               || loggedUser.getUser().getUserRole() == UserRole.ADMIN;

       if (!isOwnerOrAdmin) {
              throw new AccessDeniedException("You do not have permission to access this cost");
       }

    }
}
