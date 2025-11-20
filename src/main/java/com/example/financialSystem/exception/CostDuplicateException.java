package com.example.financialSystem.exception;

public class CostDuplicateException extends AlreadyExistsException {
    public CostDuplicateException(String message) {
        super(message);
    }
}
