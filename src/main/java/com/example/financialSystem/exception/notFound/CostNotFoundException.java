package com.example.financialSystem.exception.notFound;

import com.example.financialSystem.exception.FinancialException;

public class CostNotFoundException extends FinancialException {
    public CostNotFoundException(int id) {
        super("cost with id " + id + " not found");
    }
}
