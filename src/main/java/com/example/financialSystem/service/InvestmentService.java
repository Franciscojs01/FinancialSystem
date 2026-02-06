package com.example.financialSystem.service;

import com.example.financialSystem.exception.duplicate.InvestmentDuplicateException;
import com.example.financialSystem.exception.notFound.InvestmentNotFoundException;
import com.example.financialSystem.exception.NoChangeDetectedException;
import com.example.financialSystem.model.dto.requests.InvestmentPatchRequest;
import com.example.financialSystem.model.dto.requests.InvestmentRequest;
import com.example.financialSystem.model.dto.responses.InvestmentResponse;
import com.example.financialSystem.model.entity.Investment;
import com.example.financialSystem.model.entity.Login;
import com.example.financialSystem.model.entity.User;
import com.example.financialSystem.model.enums.FinancialType;
import com.example.financialSystem.model.enums.InvestmentType;
import com.example.financialSystem.model.enums.UserRole;
import com.example.financialSystem.model.mapper.InvestmentMapper;
import com.example.financialSystem.repository.InvestmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class InvestmentService extends UserLoggedService {
    private final InvestmentRepository investmentRepository;
    private final InvestmentMapper investmentMapper;

    public InvestmentService(InvestmentRepository investmentRepository,
                             InvestmentMapper investmentMapper) {
        this.investmentRepository = investmentRepository;
        this.investmentMapper = investmentMapper;
    }

    public InvestmentResponse createInvestment(InvestmentRequest request) {
        User user = getLoggedUser().getUser();

        Investment investment = investmentMapper.toEntity(request);
        investment.setUser(user);

        validateInvestmentDate(investment.getDateFinancial());

        investmentRepository
                .findByUserAndInvestmentTypeAndBrokerName(user, investment.getInvestmentType(), investment.getBrokerName())
                .ifPresent(existingInvestment -> {
                    if (existingInvestment.getInvestmentType().equals(InvestmentType.STOCK)) {
                        throw new InvestmentDuplicateException("You have already created investment");
                    }
                });

        recalculateFields(investment);

        return investmentMapper.toResponse(investmentRepository.save(investment));
    }

    @Transactional
    public InvestmentResponse updateInvestment(int id, InvestmentRequest request) {
        Investment existingInvestment = investmentRepository.findById(id)
                .orElseThrow(() -> new InvestmentNotFoundException(id));

        validateOwnerShip(existingInvestment);
        validateInvestmentDate(existingInvestment.getDateFinancial());

        ensureChanged(existingInvestment, request);

        investmentMapper.updateEntityFromUpdate(request, existingInvestment);

        recalculateFields(existingInvestment);
        return investmentMapper.toResponse(investmentRepository.save(existingInvestment));
    }

    @Transactional
    public InvestmentResponse patchInvestment(int id, InvestmentPatchRequest patchRequest) {
        Investment existingInvestment = investmentRepository.findById(id)
                .orElseThrow(() -> new InvestmentNotFoundException(id));

        validateOwnerShip(existingInvestment);
        validateInvestmentDate(existingInvestment.getDateFinancial());

        investmentMapper.updateEntityFromPatch(patchRequest, existingInvestment);

        recalculateFields(existingInvestment);
        return investmentMapper.toResponse(investmentRepository.save(existingInvestment));
    }

    public InvestmentResponse getInvestmentById(int id) {
        Investment investment = investmentRepository.findById(id)
                .orElseThrow(() -> new InvestmentNotFoundException(id));

        validateOwnerShip(investment);
        recalculateFields(investment);

        return investmentMapper.toResponse(investment);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<InvestmentResponse> listAllInvestments() {
        List<Investment> investments = investmentRepository.findAllActive();

        investments.forEach(this::recalculateFields);

        return investmentMapper.toResponseList(investments);
    }

    public List<InvestmentResponse> listInvestments() {
        List<Investment> investments = investmentRepository.findByUserAndDeletedFalse(getLoggedUser().getUser());

        investments.forEach(this::recalculateFields);

        return investmentMapper.toResponseList(investments);
    }

    @Transactional
    public void activateInvestment(int id) {
        Investment investment = investmentRepository.findById(id)
                .orElseThrow(() -> new InvestmentNotFoundException(id));

        validateOwnerShip(investment);

        if (!investment.getDeleted()) {
            throw new IllegalStateException("Investment is already active");
        }

        investment.setDeleted(false);
        investmentRepository.save(investment);
    }

    @Transactional
    public void deleteInvestment(int id) {
        Investment investment = investmentRepository.findById(id)
                .orElseThrow(() -> new InvestmentNotFoundException(id));

        validateOwnerShip(investment);

        if (investment.getDeleted()) {
            throw new IllegalStateException("Investment is already deleted");
        }

        investment.setDeleted(true);
        investmentRepository.save(investment);
    }

    public InvestmentResponse simulateInvestment(int id, int days) {
        if (days <= 0) {
            throw new IllegalArgumentException("Days must be greater than zero");
        }

        Investment investment = investmentRepository.findById(id)
                .orElseThrow(() -> new InvestmentNotFoundException(id));

        validateOwnerShip(investment);

        double annualRate = (investment.getInvestmentType().getRate() != null)
                ? investment.getInvestmentType().getRate()
                : investment.getBaseCurrency().getAnnualRate();

        BigDecimal futureValue =
                calculateFutureValue(investment.getValue(), annualRate, days);

        InvestmentResponse resp = investmentMapper.toResponse(investment);
        resp.setCurrentValue(futureValue);
        resp.setDaysInvested(days);

        return resp;
    }

    private BigDecimal calculateFutureValue(BigDecimal initialValue, double annualRate, long days) {

        if (days < 0) days = 0;

        double dailyRate = Math.pow(1 + annualRate, 1.0 / 365) - 1;

        BigDecimal growthFactor = BigDecimal.valueOf(Math.pow(1 + dailyRate, days));

        return initialValue.multiply(growthFactor)
                .setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateCurrentValue(Investment investment) {

        long days = ChronoUnit.DAYS.between(
                investment.getDateFinancial(),
                LocalDate.now()
        );

        double annualRate = (investment.getInvestmentType().getRate() != null)
                ? investment.getInvestmentType().getRate()
                : investment.getBaseCurrency().getAnnualRate();

        return calculateFutureValue(investment.getValue(), annualRate, days);
    }

    private void validateInvestmentDate(LocalDate date) {
        if (date.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Investment date cannot be in the future");
    }

    private void validateOwnerShip(Investment investment) {
        Login loggedInUser = getLoggedUser();

        boolean isOwnerOrAdmin = investment.getUser().getId() == loggedInUser.getUser().getId()
                || loggedInUser.getUser().getUserRole() == UserRole.ADMIN;

        if (!isOwnerOrAdmin) {
            throw new AccessDeniedException("You are not authorized to view this investment");
        }
    }

    public void ensureChanged(Investment oldInvestment, InvestmentRequest newInvestmentReq) {

        boolean unchanged =
                oldInvestment.getInvestmentType() == newInvestmentReq.investmentType()
                        && oldInvestment.getActionQuantity() == newInvestmentReq.actionQuantity()
                        && oldInvestment.getDateFinancial().isEqual(newInvestmentReq.dateFinancial())
                        && oldInvestment.getValue().compareTo(newInvestmentReq.value()) == 0
                        && oldInvestment.getBaseCurrency() == newInvestmentReq.baseCurrency()
                        && oldInvestment.getBrokerName().equals(newInvestmentReq.brokerName());

        if (unchanged) {
            throw new NoChangeDetectedException("No changes in this investment");
        }
    }


    public void recalculateFields(Investment investment) {
        investment.setCurrentValue(calculateCurrentValue(investment));
    }

}
