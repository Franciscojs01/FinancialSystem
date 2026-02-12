package com.example.financialSystem.exceptions.notFound;

import com.example.financialSystem.exceptions.FinancialException;

public class InvestmentNotFoundException extends FinancialException {
    public InvestmentNotFoundException(int id) {
        super("Investment with id " + id + " not found");
    }
}
