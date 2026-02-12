package com.example.financialSystem.exceptions.notFound;

import com.example.financialSystem.exceptions.FinancialException;

public class UserNotFoundException extends FinancialException {
    public UserNotFoundException(int id) {
        super("User with id " + id + " not found");
    }
}
