package com.example.financialSystem.model;

import com.example.financialSystem.model.enums.InvestmentType;
import com.example.financialSystem.util.BenchMarkRate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Investment extends Financial {
    @Enumerated(EnumType.STRING)
    private InvestmentType type;

    private int actionQuantity;

    @JsonIgnore
    private BigDecimal currentValue;

    private String brokerName;

    private int daysInvested;

    public Investment(InvestmentType type, BigDecimal value, BenchMarkRate baseCurrency, LocalDate dateFinancial,
                      User user, int actionQuantity, String brokerName, int daysInvested) {
        super(value, baseCurrency, dateFinancial, user);
        this.type = type;
        this.actionQuantity = actionQuantity;
        this.currentValue = value;
        this.brokerName = brokerName;
        this.daysInvested = daysInvested;
    }

    public Investment() {
    }

    public InvestmentType getType() {
        return type;
    }

    public void setType(InvestmentType type) {
        this.type = type;
    }

    public int getActionQuantity() {
        return actionQuantity;
    }

    public void setActionQuantity(int actionQuantity) {
        this.actionQuantity = actionQuantity;
    }

    public BigDecimal getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(BigDecimal currentValue) {
        this.currentValue = currentValue;
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