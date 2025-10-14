package com.example.financialSystem.dto;

import com.example.financialSystem.model.Expense;
import com.example.financialSystem.model.enums.ExpenseType;
import com.example.financialSystem.util.BenchMarkRate;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseDto {
    private ExpenseType expenseType;
    private BigDecimal value;
    private BenchMarkRate baseCurrency;
    private LocalDate expenseDate;
    private String paymentMethod;
    private boolean isFixed;

    public ExpenseDto() {

    }

    public ExpenseDto(Expense entityExpense) {
        this.expenseType = entityExpense.getType();
        this.value = entityExpense.getValue();
        this.baseCurrency = entityExpense.getBaseCurrency();
        this.expenseDate = entityExpense.getDateFinancial();
        this.paymentMethod = entityExpense.getPaymentMethod();
        this.isFixed = entityExpense.isFixed();
    }

    public ExpenseType getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(ExpenseType expenseType) {
        this.expenseType = expenseType;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public BenchMarkRate getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(BenchMarkRate baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
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
