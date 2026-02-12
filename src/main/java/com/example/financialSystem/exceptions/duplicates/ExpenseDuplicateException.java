package com.example.financialSystem.exceptions.duplicates;

import com.example.financialSystem.exceptions.AlreadyExistsException;

public class ExpenseDuplicateException extends AlreadyExistsException {
    public ExpenseDuplicateException(String message) {
        super(message);
    }
}
