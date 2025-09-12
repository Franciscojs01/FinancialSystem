package com.example.financialSystem.model;

import jakarta.persistence.Entity;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Expense extends Financial {
    private String paymentMethod;
    private boolean isFixed;

    public Expense(BigDecimal value, LocalDate dateFinancial,String baseCurrency, User user, String paymentMethod, boolean isFixed) {
        super(value,baseCurrency, dateFinancial,user);
        this.paymentMethod = paymentMethod;
        this.isFixed = isFixed;
    }


    public Expense() {

    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public boolean isFixed() {
        return isFixed;
    }

    public void setFixed(boolean fixed) {
        isFixed = fixed;
    }
}
