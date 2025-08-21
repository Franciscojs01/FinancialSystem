package com.example.financialSystem.service;

import com.example.financialSystem.dto.InvestmentDto;
import com.example.financialSystem.model.BenchMarkRate;
import com.example.financialSystem.model.Investment;
import com.example.financialSystem.model.Login;
import com.example.financialSystem.model.User;
import com.example.financialSystem.repository.InvestmentRepository;
import com.example.financialSystem.util.InterestCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class InvestmentService extends UserLoggedService{
    @Autowired
    private InvestmentRepository investmentRepository;

    public InvestmentDto createInvestment(InvestmentDto investmentDto) {
        Login login = getLoggedUser();
        User user = login.getUser();


        BenchMarkRate rate = BenchMarkRate.valueOf(investmentDto.getBaseCurrency());

        BigDecimal currentValue = InterestCalculator.calculate(
                investmentDto.getValue(),
                rate.getAnnualRate(),
                investmentDto.getDaysInvested()
        );

        Investment investment = new Investment(
                investmentDto.getType(),
                investmentDto.getValue(),
                investmentDto.getBaseCurrency(),
                investmentDto.getDateInvestment(),
                user,
                investmentDto.getActionQuantity(),
                currentValue,
                investmentDto.getBrokerName()
        );

        investmentRepository.save(investment);

        return new InvestmentDto(
                investment.getType(),
                investment.getValue(),
                investment.getBaseCurrency(),
                investment.getDateFinancial(),
                investment.getActionQuantity(),
                investment.getBrokerName(),
                investment.getDaysInvested()
        );

    }


}
