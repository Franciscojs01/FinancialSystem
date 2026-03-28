package com.example.financialSystem.exceptions.notFound;

import com.example.financialSystem.exceptions.FinancialException;

import java.util.UUID;

public class InvestmentNotFoundException extends FinancialException {
    public InvestmentNotFoundException(UUID id) {
        super("Investment with id " + id + " not found");
    }
}
