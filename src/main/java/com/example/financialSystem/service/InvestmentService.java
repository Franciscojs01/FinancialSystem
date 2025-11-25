package com.example.financialSystem.service;

import com.example.financialSystem.model.dto.requests.InvestmentPatchRequest;
import com.example.financialSystem.model.dto.requests.InvestmentRequest;
import com.example.financialSystem.model.dto.responses.InvestmentResponse;
import com.example.financialSystem.exception.InvestmentDuplicateException;
import com.example.financialSystem.exception.InvestmentNotFoundException;
import com.example.financialSystem.exception.NoChangeDetectedException;
import com.example.financialSystem.model.mapper.InvestmentMapper;
import com.example.financialSystem.model.entity.Investment;
import com.example.financialSystem.model.entity.Login;
import com.example.financialSystem.model.entity.User;
import com.example.financialSystem.model.enums.InvestmentType;
import com.example.financialSystem.repository.InvestmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;


@Service
public class InvestmentService extends UserLoggedService {
    @Autowired
    private InvestmentRepository investmentRepository;

    @Autowired
    private InvestmentMapper investmentMapper;

    public InvestmentResponse createInvestment(InvestmentRequest request) {
        User user = getLoggedUser().getUser();

        Investment investment = investmentMapper.toEntity(request);
        investment.setUser(user);

        validateInvestmentDate(investment.getDateFinancial());

        investmentRepository
                .findByUserAndInvestmentTypeAndBrokerName(user, investment.getInvestmentType(), investment.getBrokerName())
                .ifPresent(existingInvestment -> {
                    if (existingInvestment.getInvestmentType().equals(InvestmentType.STOCK) ||
                            existingInvestment.getInvestmentType().equals(InvestmentType.CRYPTO)) {
                        throw new InvestmentDuplicateException("You have already created investment");
                    }
                });

        investment.setCurrentValue(calculateCurrentValue(investment));
        investment.setDaysInvested((int) ChronoUnit.DAYS.between(
                investment.getDateFinancial(),
                LocalDate.now()
        ));

        return investmentMapper.toResponse(investmentRepository.save(investment));
    }

    public InvestmentResponse updateInvestment(int id, InvestmentRequest request) {
        Investment investment = investmentRepository.findById(id)
                .orElseThrow(() -> new InvestmentNotFoundException(id));

        validateOwnerShip(investment);
        validateInvestmentDate(investment.getDateFinancial());
        ensureChanged(investment, request);

        investmentMapper.updateEntityFromUpdate(request, investment);

        investment.setCurrentValue(calculateCurrentValue(investment));
        investment.setDaysInvested((int) ChronoUnit.DAYS.between(investment.getDateFinancial(), LocalDate.now()));

        return investmentMapper.toResponse(investmentRepository.save(investment));
    }

    public InvestmentResponse getInvestmentById(int id) {
        Investment investment = investmentRepository.findById(id)
                .orElseThrow(() -> new InvestmentNotFoundException(id));

        validateOwnerShip(investment);

        return investmentMapper.toResponse(investment);
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

        InvestmentResponse resp = new InvestmentResponse(investment);
        resp.setCurrentValue(futureValue);
        resp.setDaysInvested(days);

        return resp;
    }

    public List<InvestmentResponse> listInvestments() {
        return investmentMapper.toResponseList(investmentRepository.findByUser(getLoggedUser().getUser()));
    }

    public InvestmentResponse patchInvestment(int id, InvestmentPatchRequest patchRequest) {
        Investment existingInvestment = investmentRepository.findById(id)
                .orElseThrow(() -> new InvestmentNotFoundException(id));

        validateOwnerShip(existingInvestment);
        validateInvestmentDate(existingInvestment.getDateFinancial());

        investmentMapper.updateEntityFromPatch(patchRequest, existingInvestment);

        existingInvestment.setCurrentValue(calculateCurrentValue(existingInvestment));
        existingInvestment.setDaysInvested((int) ChronoUnit.DAYS.between(existingInvestment.getDateFinancial(), LocalDate.now()));

        return investmentMapper.toResponse(investmentRepository.save(existingInvestment));
    }

    @Transactional
    public void deleteInvestment(int id) {
        Investment investment = investmentRepository.findById(id)
                .orElseThrow(() -> new InvestmentNotFoundException(id));

        validateOwnerShip(investment);

        investmentRepository.deleteById(id);
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

    @Scheduled(cron = "0 0 0 * * ?")
    public void updateAllInvestmentsDaily() {
        List<Investment> investments = investmentRepository.findAll();

        for (Investment inv : investments) {
            inv.setDaysInvested((int) ChronoUnit.DAYS.between(inv.getDateFinancial(), LocalDate.now()));
            inv.setCurrentValue(calculateCurrentValue(inv));
        }
        investmentRepository.saveAll(investments);
    }

    private void validateInvestmentDate(LocalDate date)  {
        if (date.isAfter(LocalDate.now())) throw new IllegalArgumentException("Investment date cannot be in the future");
    }

    private void validateOwnerShip(Investment investment) {
        Login loggedInUser = getLoggedUser();

        if (investment.getUser().getId() != loggedInUser.getUser().getId()) {
            throw new AccessDeniedException("You are not authorized to view this investment");
        }
    }

    public void ensureChanged(Investment oldInvestment, InvestmentRequest newInvReq) {
       InvestmentRequest oldAsRequest = investmentMapper.toRequest(oldInvestment);

       if (oldAsRequest.equals(newInvReq)) throw new NoChangeDetectedException("No changes in this investment");

    }


}
