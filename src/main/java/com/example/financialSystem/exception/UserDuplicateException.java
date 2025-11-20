package com.example.financialSystem.exception;

public class UserDuplicateException extends AlreadyExistsException {
    public UserDuplicateException(String field, String value) {
        super("User with " + field + " " + value + " already exists");
    }
}
