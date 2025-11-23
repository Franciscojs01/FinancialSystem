package com.example.financialSystem.dto.responses;

import com.example.financialSystem.model.Investment;
import com.example.financialSystem.model.enums.InvestmentType;
import com.example.financialSystem.util.BenchMarkRate;
import lombok.AllArgsConstructor;
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
    private int idInvestment;
    private InvestmentType type;
    private BigDecimal value;
    private BenchMarkRate baseCurrency;
    private LocalDate dateFinancial;
    private int actionQuantity;
    private BigDecimal currentValue;
    private String brokerName;
    private int daysInvested;

    public InvestmentResponse(Investment entityInvestment) {
        this.idInvestment = entityInvestment.getId();
        this.type = entityInvestment.getType();
        this.value = entityInvestment.getValue();
        this.baseCurrency = entityInvestment.getBaseCurrency();
        this.dateFinancial = entityInvestment.getDateFinancial();
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
