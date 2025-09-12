package com.example.financialSystem.service;

import com.example.financialSystem.dto.InvestmentDto;

import com.example.financialSystem.exceptions.InvestmentDuplicateException;
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

        if (type.equals(InvestmentType.STOCK) ||  type.equals(InvestmentType.CRYPTO)) {
            throw new InvestmentDuplicateException("You have already created investment");
        }

        if (Objects.equals(foundInvestment.getDateFinancial(), newInvestment.getDateFinancial())) {
            throw new InvestmentDuplicateException("You have already a investment with this date");
        }

        Investment savedInvestment = investmentRepository.save(newInvestment);
        return new InvestmentDto(savedInvestment);
    }

}
