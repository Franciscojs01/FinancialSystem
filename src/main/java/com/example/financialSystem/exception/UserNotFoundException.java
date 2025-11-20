package com.example.financialSystem.exception;

public class UserNotFoundException extends FinancialException {
    public UserNotFoundException(int id) {
        super("User with id " + id + " not found");
    }
}
