package com.example.financialSystem.service;

import com.example.financialSystem.dto.InvestmentResponse;
import com.example.financialSystem.exceptions.InvestmentDuplicateException;
import com.example.financialSystem.exceptions.InvestmentNotFoundException;
import com.example.financialSystem.exceptions.NoChangeDetectedException;
import com.example.financialSystem.model.Investment;
import com.example.financialSystem.model.Login;
import com.example.financialSystem.model.User;
import com.example.financialSystem.model.enums.InvestmentType;
import com.example.financialSystem.repository.InvestmentRepository;
import com.example.financialSystem.util.BenchMarkRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Service
public class InvestmentService extends UserLoggedService {
    @Autowired
    private InvestmentRepository investmentRepository;

    public InvestmentResponse createInvestment(Investment investment) {
        User user = getLoggedUser().getUser();
        investment.setUser(user);

        validateInvestmentFields(investment);

        validateInvestmentDate(investment);

        investmentRepository
                .findByUserAndTypeAndBrokerName(user, investment.getType(), investment.getBrokerName())
                .ifPresent(existingInvestment -> {
                    if (existingInvestment.getType().equals(InvestmentType.STOCK) ||
                            existingInvestment.getType().equals(InvestmentType.CRYPTO)) {
                        throw new InvestmentDuplicateException("You have already created investment");
                    }
                });

        investment.setCurrentValue(calculateCurrentValue(investment));

        Investment savedInvestment = investmentRepository.save(investment);
        return new InvestmentResponse(savedInvestment);
    }

    public InvestmentResponse editInvestment(int id, Investment updatedInvestment) {
        Investment existingInvestment = investmentRepository.findById(id)
                .orElseThrow(() -> new InvestmentNotFoundException("Investment with Id " + id + "Not found"));

        validateOnwerShip(existingInvestment);

        ensureChanged(existingInvestment, updatedInvestment);

        existingInvestment.setType(updatedInvestment.getType());
        existingInvestment.setValue(updatedInvestment.getValue());
        existingInvestment.setBaseCurrency(updatedInvestment.getBaseCurrency());
        existingInvestment.setDateFinancial(updatedInvestment.getDateFinancial());
        existingInvestment.setActionQuantity(updatedInvestment.getActionQuantity());
        existingInvestment.setBrokerName(updatedInvestment.getBrokerName());
        existingInvestment.setCurrentValue(calculateCurrentValue(existingInvestment));

        Investment updatedData = investmentRepository.save(existingInvestment);
        return new InvestmentResponse(updatedData);
    }


    public InvestmentResponse getInvestmentById(int id) {
        Investment investment = investmentRepository.findById(id)
                .orElseThrow(() -> new InvestmentNotFoundException("Investment with Id " + id + "Not found"));

        validateOnwerShip(investment);

        return new InvestmentResponse(investment);
    }

    public InvestmentResponse simulateInvestment(int id, int days) {
        Investment investment = investmentRepository.findById(id)
                .orElseThrow(() -> new InvestmentNotFoundException("Investment with Id " + id + " not found"));

        validateOnwerShip(investment);

        BigDecimal initialValue = investment.getValue();
        InvestmentType type = investment.getType();
        BenchMarkRate baseCurrency = investment.getBaseCurrency();

        double rate = (type.getRate() != null) ? type.getRate() : baseCurrency.getAnnualRate();

        double dailyRate = Math.pow(1 + rate, 1.0 / 365) - 1;

        BigDecimal growthFactor = BigDecimal.valueOf(Math.pow(1 + dailyRate, days));
        BigDecimal futureValue = initialValue.multiply(growthFactor).setScale(4, java.math.RoundingMode.HALF_UP);

        InvestmentResponse investmentDto = new InvestmentResponse(investment);
        investmentDto.setCurrentValue(futureValue);

        investmentDto.setDaysInvested(days);

        return investmentDto;
    }


    public List<Investment> listInvestments() {
        return investmentRepository.findByUser(getLoggedUser().getUser());
    }

    public void deleteInvestment(long id) {
        Investment investment = investmentRepository.findById(id)
                .orElseThrow(() -> new InvestmentNotFoundException("Investment with Id " + id + "Not found"));

        validateOnwerShip(investment);

        investmentRepository.deleteById(id);
    }

    private BigDecimal calculateCurrentValue(Investment investment) {
        BigDecimal initialValue = investment.getValue();
        InvestmentType type = investment.getType();
        BenchMarkRate baseCurrency = investment.getBaseCurrency();

        double rate;

        if (type.getRate() != null) {
            rate = type.getRate();
        } else {
            rate = baseCurrency.getAnnualRate();
        }

        double dailyRate = Math.pow(1 + rate, 1.0 / 365) - 1;

        long days = java.time.temporal.ChronoUnit.DAYS.between(
                investment.getDateFinancial(),
                java.time.LocalDate.now()
        );

        if (days < 0) days = 0;

        BigDecimal growthFactor = BigDecimal.valueOf(Math.pow(1 + dailyRate, days));
        BigDecimal futureValue = initialValue.multiply(growthFactor);

        return futureValue.setScale(4, java.math.BigDecimal.ROUND_HALF_UP);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void updateAllInvestmentsDaily() {
        List<Investment> investments = investmentRepository.findAll();

        for (Investment inv : investments) {
            inv.setDaysInvested(inv.getDaysInvested() + 1);
            inv.setCurrentValue(calculateCurrentValue(inv));
        }
        investmentRepository.saveAll(investments);
    }

    private void validateInvestmentFields(Investment investment) {
        if (investment.getBrokerName() == null || investment.getBrokerName().isBlank()) {
            throw new IllegalArgumentException("broker name is required");
        }

        if (investment.getActionQuantity() <= 0) {
            throw new IllegalArgumentException("Action quantity must be greater than zero");
        }
    }

    private void validateInvestmentDate(Investment investment) {
        if (investment.getDateFinancial().isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Investment date cannot be in the future");
    }

    private void validateOnwerShip(Investment investment) {
        Login loggedInUser = getLoggedUser();

        if (investment.getUser().getId() != loggedInUser.getId()) {
            throw new AccessDeniedException("You are not authorized to view this investment");
        }
    }

    public void ensureChanged(Investment existingInvestment, Investment updatedInvestment) {
        boolean unchanged =
                existingInvestment.getType().equals(updatedInvestment.getType()) &&
                        existingInvestment.getValue().compareTo(updatedInvestment.getValue()) == 0 &&
                        existingInvestment.getBaseCurrency().equals(updatedInvestment.getBaseCurrency()) &&
                        existingInvestment.getDateFinancial().equals(updatedInvestment.getDateFinancial()) &&
                        existingInvestment.getActionQuantity() == updatedInvestment.getActionQuantity() &&
                        existingInvestment.getBrokerName().equals(updatedInvestment.getBrokerName());

        if (unchanged) {
            throw new NoChangeDetectedException("No changes in this investment");
        }

    }


}
