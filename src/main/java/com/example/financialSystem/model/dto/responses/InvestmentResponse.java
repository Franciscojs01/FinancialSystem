package com.example.financialSystem.model.dto.responses;

import com.example.financialSystem.model.entity.Investment;
import com.example.financialSystem.model.enums.InvestmentType;
import com.example.financialSystem.util.BenchMarkRate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@NoArgsConstructor
public class InvestmentResponse {
    private int id;
    private InvestmentType investmentType;
    private BigDecimal value;
    private LocalDate dateFinancial;
    private BenchMarkRate baseCurrency;
    private int actionQuantity;
    private BigDecimal currentValue;
    private String brokerName;
    private int daysInvested;

    public InvestmentResponse(Investment entityInvestment) {
        this.id = entityInvestment.getId();
        this.investmentType = entityInvestment.getInvestmentType();
        this.value = entityInvestment.getValue();
        this.dateFinancial = entityInvestment.getDateFinancial();
        this.baseCurrency = entityInvestment.getBaseCurrency();
        this.actionQuantity = entityInvestment.getActionQuantity();
        this.currentValue = entityInvestment.getCurrentValue();
        this.brokerName = entityInvestment.getBrokerName();
        this.daysInvested = entityInvestment.getDaysInvested();

        if (entityInvestment.getDateFinancial() != null) {
            this.daysInvested = (int) ChronoUnit.DAYS.between(dateFinancial, LocalDate.now());
        } else {
            this.daysInvested = 0;
        }
    }

}
