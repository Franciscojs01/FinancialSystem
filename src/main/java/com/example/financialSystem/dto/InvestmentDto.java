package com.example.financialSystem.dto;

import com.example.financialSystem.model.Investment;
import com.example.financialSystem.model.enums.InvestmentType;
import com.example.financialSystem.util.BenchMarkRate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


public class InvestmentDto {
    private InvestmentType type;
    private BigDecimal value;
    private BenchMarkRate baseCurrency;
    private LocalDate dateFinancial;
    private int actionQuantity;
    private BigDecimal currentValue;
    private String brokerName;
    private int daysInvested;

    public InvestmentDto() {

    }

    public InvestmentDto(Investment entityInvestment) {
        this.type = entityInvestment.getType();
        this.value = entityInvestment.getValue();
        this.baseCurrency = entityInvestment.getBaseCurrency();
        this.dateFinancial = entityInvestment.getDateFinancial();
        this.actionQuantity = entityInvestment.getActionQuantity();
        this.currentValue = entityInvestment.getCurrentValue();
        this.brokerName = entityInvestment.getBrokerName();

        if (entityInvestment.getDateFinancial() != null) {
            this.daysInvested = (int) ChronoUnit.DAYS.between(dateFinancial, LocalDate.now());
        } else {
            this.daysInvested = 0;
        }
    }

    public InvestmentType getType() {
        return type;
    }

    public BigDecimal getValue() {
        return value;
    }

    public BenchMarkRate getBaseCurrency() {
        return baseCurrency;
    }

    public LocalDate getDateFinancial() {
        return dateFinancial;
    }

    public int getActionQuantity() {
        return actionQuantity;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public int getDaysInvested() {
        return daysInvested;
    }

    public void setType(InvestmentType type) {
        this.type = type;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public void setBaseCurrency(BenchMarkRate baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public void setDateFinancial(LocalDate dateFinancial) {
        this.dateFinancial = dateFinancial;
    }

    public void setActionQuantity(int actionQuantity) {
        this.actionQuantity = actionQuantity;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public void setDaysInvested(int daysInvested) {
        this.daysInvested = daysInvested;
    }

    public BigDecimal getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(BigDecimal currentValue) {
        this.currentValue = currentValue;
    }
}
