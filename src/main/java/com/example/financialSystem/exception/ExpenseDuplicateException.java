package com.example.financialSystem.exception;

public class ExpenseDuplicateException extends AlreadyExistsException {
    public ExpenseDuplicateException(String message) {
        super(message);
    }
}
