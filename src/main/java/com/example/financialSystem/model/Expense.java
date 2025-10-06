package com.example.financialSystem.model;

import com.example.financialSystem.model.enums.ExpensesType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Expense extends Financial {
    @Enumerated(EnumType.STRING)
    private ExpensesType type;

    private String paymentMethod;
    private boolean isFixed;

    public Expense(ExpensesType type, BigDecimal value, LocalDate dateFinancial,String baseCurrency, User user, String paymentMethod, boolean isFixed) {
        super(value,baseCurrency, dateFinancial,user);
        this.type = type;
        this.paymentMethod = paymentMethod;
        this.isFixed = isFixed;
    }

    public Expense() {

    }

    public ExpensesType getType() {
        return type;
    }

    public void setType(ExpensesType type) {
        this.type = type;
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
