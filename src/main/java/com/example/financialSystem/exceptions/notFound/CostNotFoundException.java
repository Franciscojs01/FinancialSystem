package com.example.financialSystem.exceptions.notFound;

import com.example.financialSystem.exceptions.FinancialException;

public class CostNotFoundException extends FinancialException {
    public CostNotFoundException(int id) {
        super("cost with id " + id + " not found");
    }
}
