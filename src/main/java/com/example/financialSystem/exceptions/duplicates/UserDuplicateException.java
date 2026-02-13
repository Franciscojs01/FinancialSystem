package com.example.financialSystem.exceptions.duplicates;

import com.example.financialSystem.exceptions.AlreadyExistsException;

public class UserDuplicateException extends AlreadyExistsException {
    public UserDuplicateException(String field, String value) {
        super("User with " + field + " " + value + " already exists");
    }
}
