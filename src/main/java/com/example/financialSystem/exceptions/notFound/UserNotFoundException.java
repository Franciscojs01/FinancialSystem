package com.example.financialSystem.exceptions.notFound;

import com.example.financialSystem.exceptions.FinancialException;

import java.util.UUID;

public class UserNotFoundException extends FinancialException {
    public UserNotFoundException(UUID id) {
        super("User with id " + id + " not found");
    }
}
