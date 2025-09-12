package com.example.financialSystem.exceptions;

public class InvestmentDuplicateException extends RuntimeException {
    public InvestmentDuplicateException(String message) {
        super(message);
    }
}
