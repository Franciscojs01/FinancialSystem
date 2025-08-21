package com.example.financialSystem.dto;


import java.math.BigDecimal;
import java.time.LocalDate;

public class InvestmentDto {
    private String type;
    private BigDecimal value;
    private String baseCurrency;
    private LocalDate dateInvestment;
    private BigDecimal actionQuantity;
    private String brokerName;
    private int daysInvested;

    public InvestmentDto(String type, BigDecimal value, String baseCurrency, LocalDate dateInvestment, BigDecimal actionQuantity, String brokerName, int daysInvested) {
        this.type = type;
        this.value = value;
        this.baseCurrency = baseCurrency;
        this.dateInvestment = dateInvestment;
        this.actionQuantity = actionQuantity;
        this.brokerName = brokerName;
        this.daysInvested = daysInvested;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public LocalDate getDateInvestment() {
        return dateInvestment;
    }

    public void setDateInvestment(LocalDate dateInvestment) {
        this.dateInvestment = dateInvestment;
    }

    public BigDecimal getActionQuantity() {
        return actionQuantity;
    }

    public void setActionQuantity(BigDecimal actionQuantity) {
        this.actionQuantity = actionQuantity;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public int getDaysInvested() {
        return daysInvested;
    }

    public void setDaysInvested(int daysInvested) {
        this.daysInvested = daysInvested;
    }
}
