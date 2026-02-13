package com.example.financialSystem.exceptions.duplicates;

import com.example.financialSystem.exceptions.AlreadyExistsException;

public class CostDuplicateException extends AlreadyExistsException {
    public CostDuplicateException(String message) {
        super(message);
    }
}
