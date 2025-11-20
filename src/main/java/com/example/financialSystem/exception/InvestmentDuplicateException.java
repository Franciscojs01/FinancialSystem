package com.example.financialSystem.exception;

public class InvestmentDuplicateException extends AlreadyExistsException {
    public InvestmentDuplicateException(String message) {
        super(message);
    }
}
