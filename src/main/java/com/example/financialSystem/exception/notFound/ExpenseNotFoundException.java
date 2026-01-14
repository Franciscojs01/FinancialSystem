package com.example.financialSystem.exception.notFound;

import com.example.financialSystem.exception.FinancialException;

public class ExpenseNotFoundException extends FinancialException {
    public ExpenseNotFoundException(int id) {
        super("expense with id " + id + " not found");
    }
}
