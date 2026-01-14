package com.example.financialSystem.exception.duplicate;

import com.example.financialSystem.exception.AlreadyExistsException;

public class ExpenseDuplicateException extends AlreadyExistsException {
    public ExpenseDuplicateException(String message) {
        super(message);
    }
}
