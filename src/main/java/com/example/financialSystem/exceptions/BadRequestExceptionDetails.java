package com.example.financialSystem.exceptions;

import lombok.Data;

@Data
public class BadRequestExceptionDetails {
    private int statusCode;
    private String errorName;
    private String errorMessage;

    public BadRequestExceptionDetails(int statusCode, String errorName, String erorMessage) {
        this.statusCode = statusCode;
        this.errorName = errorName;
        this.errorMessage = erorMessage;
    }
}
