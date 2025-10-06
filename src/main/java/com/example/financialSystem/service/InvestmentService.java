package com.example.financialSystem.service;

import com.example.financialSystem.dto.InvestmentDto;

import com.example.financialSystem.exceptions.InvestmentDuplicateException;
import com.example.financialSystem.exceptions.InvestmentNotFoundException;
import com.example.financialSystem.model.*;

import java.math.BigDecimal;
import java.util.List;

import com.example.financialSystem.model.enums.InvestmentType;
import com.example.financialSystem.repository.InvestmentRepository;
import com.example.financialSystem.util.BenchMarkRate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class InvestmentService extends UserLoggedService {
    @Autowired
    private InvestmentRepository investmentRepository;

    public InvestmentDto createInvestment(Investment newInvestment) {
        User user = getLoggedUser().getUser();
        newInvestment.setUser(user);

        investmentRepository
                .findByUserAndTypeAndBrokerName(user, newInvestment.getType(), newInvestment.getBrokerName())
                .ifPresent(existing -> {
                    if (existing.getType().equals(InvestmentType.STOCK) || existing.getType().equals(InvestmentType.CRYPTO)) {
                        throw new InvestmentDuplicateException("You have already created investment");
                    }
                });

        Investment savedInvestment = investmentRepository.save(newInvestment);
        return new InvestmentDto(savedInvestment);
    }

    public InvestmentDto editInvestment(int id, InvestmentDto editInvestmentDto) {

        Investment existingInvestment = investmentRepository.findById(id)
                .orElseThrow(() -> new InvestmentNotFoundException("Investment with Id " + id + "Not found"));

        Login loggedInUser = getLoggedUser();

        if (existingInvestment.getUser().getId() != loggedInUser.getId()) {
            throw new AccessDeniedException("You are not authorized to edit this investment");
        }

        unchanged(existingInvestment, editInvestmentDto);

        existingInvestment.setType(editInvestmentDto.getType());
        existingInvestment.setValue(editInvestmentDto.getValue());
        existingInvestment.setBaseCurrency(editInvestmentDto.getBaseCurrency());
        existingInvestment.setDateFinancial(editInvestmentDto.getDateFinancial());
        existingInvestment.setActionQuantity(editInvestmentDto.getActionQuantity());
        existingInvestment.setCurrentValue(editInvestmentDto.getCurrentValue());
        existingInvestment.setBrokerName(editInvestmentDto.getBrokerName());

        Investment updatedInvestment = investmentRepository.save(existingInvestment);

        return new InvestmentDto(updatedInvestment);
    }

    public Boolean unchanged(Investment existingInvestment, InvestmentDto editInvestmentDto) {
        boolean unchanged =
                existingInvestment.getType().equals(editInvestmentDto.getType()) &&
                        existingInvestment.getValue().compareTo(editInvestmentDto.getValue()) == 0 &&
                        existingInvestment.getBaseCurrency().equals(editInvestmentDto.getBaseCurrency()) &&
                        existingInvestment.getDateFinancial().equals(editInvestmentDto.getDateFinancial()) &&
                        existingInvestment.getActionQuantity() == editInvestmentDto.getActionQuantity() &&
                        existingInvestment.getCurrentValue().compareTo(editInvestmentDto.getCurrentValue()) == 0 &&
                        existingInvestment.getBrokerName().equals(editInvestmentDto.getBrokerName());

        if (unchanged) {
            throw new InvestmentDuplicateException("You didn't change anything in this investment");

        }

        return false;
    }

    public InvestmentDto getInvestmentById(int id) {
        Investment investment = investmentRepository.findById(id).orElseThrow(() -> new InvestmentNotFoundException("Investment with Id " + id + "Not found"));

        return new InvestmentDto(investment);
    }

    public InvestmentDto simulateInvestment(int id, int days) {
        Investment investment = investmentRepository.findById(id)
                .orElseThrow(() -> new InvestmentNotFoundException("Investment with Id " + id + "Not found"));

        BigDecimal initialValue = investment.getValue();

        BenchMarkRate rate = BenchMarkRate.valueOf(investment.getBaseCurrency());
        double annualRate = rate.getAnnualRate();

        double dailyRate = Math.pow(1 + annualRate, 1.0 / 365) - 1;

        double futureValue = Math.round(initialValue.doubleValue() * Math.pow(1 + dailyRate, days));

        InvestmentDto investmentDto = new InvestmentDto(investment);
        investmentDto.setCurrentValue(BigDecimal.valueOf(futureValue));

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


}
