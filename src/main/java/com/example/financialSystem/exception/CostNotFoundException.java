package com.example.financialSystem.exception;

public class CostNotFoundException extends FinancialException {
    public CostNotFoundException(int id) {
        super("cost with id " + id + " not found");
    }
}
