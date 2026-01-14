package com.example.financialSystem.exception.duplicate;

import com.example.financialSystem.exception.AlreadyExistsException;

public class UserDuplicateException extends AlreadyExistsException {
    public UserDuplicateException(String field, String value) {
        super("User with " + field + " " + value + " already exists");
    }
}
