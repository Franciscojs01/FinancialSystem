package com.example.financialSystem.model;

import jakarta.persistence.Entity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
public class Investment extends Financial {
    private BigDecimal actionQuantity;
    private BigDecimal currentValue;
    private String brokerName;

    public Investment(String type, BigDecimal value, String baseCurrency, LocalDate dateFinancial, User user, BigDecimal actionQuantity, BigDecimal currentValue, String brokerName) {
        super(type, value, baseCurrency, dateFinancial, user);
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