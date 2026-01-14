package com.example.financialSystem.exception.duplicate;

import com.example.financialSystem.exception.AlreadyExistsException;

public class CostDuplicateException extends AlreadyExistsException {
    public CostDuplicateException(String message) {
        super(message);
    }
}
