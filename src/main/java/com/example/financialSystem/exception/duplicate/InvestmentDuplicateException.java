package com.example.financialSystem.exception.duplicate;

import com.example.financialSystem.exception.AlreadyExistsException;

public class InvestmentDuplicateException extends AlreadyExistsException {
    public InvestmentDuplicateException(String message) {
        super(message);
    }
}
