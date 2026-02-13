package com.example.financialSystem.exceptions.notFound;

import com.example.financialSystem.exceptions.FinancialException;

public class ExpenseNotFoundException extends FinancialException {
    public ExpenseNotFoundException(int id) {
        super("expense with id " + id + " not found");
    }
}
