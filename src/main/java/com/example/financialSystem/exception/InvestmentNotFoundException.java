package com.example.financialSystem.exception;

public class InvestmentNotFoundException extends FinancialException {
    public InvestmentNotFoundException(int id) {
        super("Investment with id " + id + " not found");
    }
}
