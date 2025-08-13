package com.example.financialSystem.model;

import jakarta.persistence.Entity;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Investment extends Financial {
    private BigDecimal actionQuantity;
    private BigDecimal currentValue;
    private String brokerName;

    public Investment(String type, BigDecimal value, LocalDate dateFinancial, User user, BigDecimal actionQuantity, BigDecimal currentValue, String brokerName) {
        super(type, value, dateFinancial, user);
        this.actionQuantity = actionQuantity;
        this.currentValue = currentValue;
        this.brokerName = brokerName;
    }

    public Investment() {
    }

    public BigDecimal getActionQuantity() {
        return actionQuantity;
    }

    public void setActionQuantity(BigDecimal actionQuantity) {
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
