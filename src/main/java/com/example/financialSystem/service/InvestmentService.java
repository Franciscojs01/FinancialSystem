package com.example.financialSystem.service;

import com.example.financialSystem.dto.InvestmentDto;

import com.example.financialSystem.exceptions.InvestmentDuplicateException;
import com.example.financialSystem.exceptions.InvestmentNotFoundException;
import com.example.financialSystem.model.Investment;
import com.example.financialSystem.model.InvestmentType;

import java.util.Objects;

import com.example.financialSystem.model.User;
import com.example.financialSystem.repository.InvestmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class InvestmentService extends UserLoggedService {
    @Autowired
    private InvestmentRepository investmentRepository;

    public InvestmentDto createInvestment(Investment newInvestment) {
        User user = getLoggedUser().getUser();
        newInvestment.setUser(user);

        Optional<Investment> existingInvestment = investmentRepository
                .findByUserAndTypeAndBrokerName(user, newInvestment.getType(), newInvestment.getBrokerName());

        if (existingInvestment.isEmpty()) {
            Investment savedInvestment = investmentRepository.save(newInvestment);
            return new InvestmentDto(savedInvestment);
        }

        Investment foundInvestment = existingInvestment.get();
        InvestmentType type = foundInvestment.getType();

        if (type.equals(InvestmentType.STOCK) || type.equals(InvestmentType.CRYPTO)) {
            throw new InvestmentDuplicateException("You have already created investment");
        }

        if (Objects.equals(foundInvestment.getDateFinancial(), newInvestment.getDateFinancial())) {
            throw new InvestmentDuplicateException("You have already a investment with this date");
        }

        Investment savedInvestment = investmentRepository.save(newInvestment);
        return new InvestmentDto(savedInvestment);
    }

    public InvestmentDto editInvestment(int id, InvestmentDto editInvestmentDto) {
        Investment existingInvestment = investmentRepository.findById(id)
                .orElseThrow(() -> new InvestmentNotFoundException("Investment with Id " + id + "Not found"));

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

}
