package com.example.financialSystem.exception.notFound;

import com.example.financialSystem.exception.FinancialException;

public class UserNotFoundException extends FinancialException {
    public UserNotFoundException(int id) {
        super("User with id " + id + " not found");
    }
}
