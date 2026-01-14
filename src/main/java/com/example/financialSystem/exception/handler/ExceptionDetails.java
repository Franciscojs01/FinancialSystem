package com.example.financialSystem.exception.handler;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
public class ExceptionDetails {
    protected String title;
    protected int statusCode;
    protected String details;
    protected String developerMessage;
    protected LocalDateTime timestamp;
}
