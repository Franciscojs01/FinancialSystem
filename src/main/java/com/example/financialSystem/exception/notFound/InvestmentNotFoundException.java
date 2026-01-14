package com.example.financialSystem.exception.notFound;

import com.example.financialSystem.exception.FinancialException;

public class InvestmentNotFoundException extends FinancialException {
    public InvestmentNotFoundException(int id) {
        super("Investment with id " + id + " not found");
    }
}
