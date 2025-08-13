package com.example.financialSystem.model;

import jakarta.persistence.Entity;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Expenses extends Financial {
    private String paymentMethod;
    private boolean isFixed;

    public Expenses(String type, BigDecimal value, LocalDate dateFinancial, User user, String paymentMethod, boolean isFixed) {
        super(type, value, dateFinancial, user);
        this.paymentMethod = paymentMethod;
        this.isFixed = isFixed;
    }


    public Expenses() {

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
