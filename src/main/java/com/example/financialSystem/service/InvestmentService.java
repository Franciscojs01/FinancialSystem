package com.example.financialSystem.service;

import com.example.financialSystem.dto.InvestmentDto;
import com.example.financialSystem.dto.InvestmentRequestDto;
import com.example.financialSystem.exceptions.InvestmentDuplicateException;
import com.example.financialSystem.exceptions.InvestmentNotFoundException;
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
import java.util.List;


@Service
public class InvestmentService extends UserLoggedService {
    @Autowired
    private InvestmentRepository investmentRepository;

    public InvestmentDto createInvestment(Investment investment) {
        User user = getLoggedUser().getUser();
        investment.setUser(user);

        if (investment.getBrokerName() == null || investment.getActionQuantity() <= 0) {
            throw new IllegalArgumentException("broker name, current value and action quantity are required");
        }

        if (investment.getDateFinancial().isAfter(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("You cannot register an investment in the future");
        }

        investmentRepository
                .findByUserAndTypeAndBrokerName(user, investment.getType(), investment.getBrokerName())
                .ifPresent(existing -> {
                    if (existing.getType().equals(InvestmentType.STOCK) || existing.getType().equals(InvestmentType.CRYPTO)) {
                        throw new InvestmentDuplicateException("You have already created investment");
                    }
                });

        BigDecimal currentValue = calculateCurrentValue(investment);
        investment.setCurrentValue(currentValue);

        Investment savedInvestment = investmentRepository.save(investment);
        return new InvestmentDto(savedInvestment);
    }

    public InvestmentDto editInvestment(int id, InvestmentRequestDto investmentRequestDto) {

        Investment existingInvestment = investmentRepository.findById(id)
                .orElseThrow(() -> new InvestmentNotFoundException("Investment with Id " + id + "Not found"));

        Login loggedInUser = getLoggedUser();

        if (existingInvestment.getUser().getId() != loggedInUser.getId()) {
            throw new AccessDeniedException("You are not authorized to edit this investment");
        }

        unchanged(existingInvestment, investmentRequestDto);

        existingInvestment.setType(investmentRequestDto.type());
        existingInvestment.setValue(investmentRequestDto.value());
        existingInvestment.setBaseCurrency(investmentRequestDto.baseCurrency());
        existingInvestment.setDateFinancial(investmentRequestDto.dateFinancial());
        existingInvestment.setActionQuantity(investmentRequestDto.actionQuantity());
        existingInvestment.setBrokerName(investmentRequestDto.brokerName());
        existingInvestment.setCurrentValue(calculateCurrentValue(existingInvestment));

        Investment updatedInvestment = investmentRepository.save(existingInvestment);
        return new InvestmentDto(updatedInvestment);
    }

    public Boolean unchanged(Investment existingInvestment, InvestmentRequestDto investmentRequestDto) {
        boolean unchanged =
                existingInvestment.getType().equals(investmentRequestDto.type()) &&
                        existingInvestment.getValue().compareTo(investmentRequestDto.value()) == 0 &&
                        existingInvestment.getBaseCurrency().equals(investmentRequestDto.baseCurrency()) &&
                        existingInvestment.getDateFinancial().equals(investmentRequestDto.dateFinancial()) &&
                        existingInvestment.getActionQuantity() == investmentRequestDto.actionQuantity() &&
                        existingInvestment.getBrokerName().equals(investmentRequestDto.brokerName());

        if (unchanged) {
            throw new IllegalArgumentException("You didn't change anything in this investment");
        }

        return false;
    }

    public InvestmentDto getInvestmentById(int id) {
        Investment investment = investmentRepository.findById(id).orElseThrow(() -> new InvestmentNotFoundException("Investment with Id " + id + "Not found"));

        return new InvestmentDto(investment);
    }

    public InvestmentDto simulateInvestment(int id, int days) {
        Investment investment = investmentRepository.findById(id)
                .orElseThrow(() -> new InvestmentNotFoundException("Investment with Id " + id + " not found"));

        BigDecimal initialValue = investment.getValue();
        InvestmentType type = investment.getType();
        BenchMarkRate baseCurrency = investment.getBaseCurrency();

        double rate = (type.getRate() != null) ? type.getRate() : baseCurrency.getAnnualRate();

        double dailyRate = Math.pow(1 + rate, 1.0 / 365) - 1;

        BigDecimal growthFactor = BigDecimal.valueOf(Math.pow(1 + dailyRate, days));
        BigDecimal futureValue = initialValue.multiply(growthFactor).setScale(4, java.math.RoundingMode.HALF_UP);

        InvestmentDto investmentDto = new InvestmentDto(investment);
        investmentDto.setCurrentValue(futureValue);

        return investmentDto;
    }


    public List<Investment> listInvestments() {
        Login loggedInUser = getLoggedUser();
        User user = loggedInUser.getUser();

        return investmentRepository.findByUser(user);
    }

    public void deleteInvestment(long id) {
        Investment investment = investmentRepository.findById(id)
                .orElseThrow(() -> new InvestmentNotFoundException("Investment with Id " + id + "Not found"));

        Login loggedInUser = getLoggedUser();

        if (loggedInUser.getUser().getId() != investment.getUser().getId()) {
            throw new AccessDeniedException("You are not authorized to delete this investment");
        }
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
            inv.setCurrentValue(calculateCurrentValue(inv));
        }
        investmentRepository.saveAll(investments);
    }



}
