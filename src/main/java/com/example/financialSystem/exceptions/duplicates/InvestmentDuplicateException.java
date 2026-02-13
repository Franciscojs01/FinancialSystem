package com.example.financialSystem.exceptions.duplicates;

import com.example.financialSystem.exceptions.AlreadyExistsException;

public class InvestmentDuplicateException extends AlreadyExistsException {
    public InvestmentDuplicateException(String message) {
        super(message);
    }
}
