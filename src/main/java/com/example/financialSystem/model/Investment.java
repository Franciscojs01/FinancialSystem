package com.example.financialSystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
public class Investment extends Financial {
    @Enumerated(EnumType.STRING)
    private InvestmentType type;

    private int actionQuantity;
    private BigDecimal currentValue;
    private String brokerName;

    public Investment(InvestmentType type, BigDecimal value, String baseCurrency, LocalDate dateFinancial,
                      User user, int actionQuantity, BigDecimal currentValue, String brokerName) {
        super(value, baseCurrency, dateFinancial, user);
        this.type = type;
        this.actionQuantity = actionQuantity;
        this.currentValue = currentValue;
        this.brokerName = brokerName;
    }



    public Investment() {
    }


    public int getDaysInvested() {
        if (this.getDateFinancial() == null) {
            return 0;
        }

        return (int) ChronoUnit.DAYS.between(this.getDateFinancial(), LocalDate.now());
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



}