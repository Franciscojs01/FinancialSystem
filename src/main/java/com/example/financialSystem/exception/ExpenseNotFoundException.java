package com.example.financialSystem.exception;

public class ExpenseNotFoundException extends FinancialException {
    public ExpenseNotFoundException(int id) {
        super("expense with id " + id + " not found");
    }
}
