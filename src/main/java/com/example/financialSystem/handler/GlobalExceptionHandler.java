package com.example.financialSystem.handler;

import com.example.financialSystem.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<BadRequestExceptionDetails> handleUserNotFoundException(UserNotFoundException e, WebRequest webRequest) {
        BadRequestExceptionDetails errorResponse = new BadRequestExceptionDetails(
                HttpStatus.NOT_FOUND.value(), "User not found", e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(UserDuplicateException.class)
    public ResponseEntity<BadRequestExceptionDetails> handleUserDuplicateException(UserDuplicateException e, WebRequest webRequest) {
        BadRequestExceptionDetails errorResponse = new BadRequestExceptionDetails(
                HttpStatus.CONFLICT.value(), "User duplicate", e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException e, WebRequest webRequest) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneral(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred: " + ex.getMessage());
    }

    @ExceptionHandler(InvestmentNotFoundException.class)
    public ResponseEntity<BadRequestExceptionDetails> handleInvestmentNotFoundException(InvestmentNotFoundException e, WebRequest webRequest) {
        BadRequestExceptionDetails errorResponse = new BadRequestExceptionDetails(
                HttpStatus.NOT_FOUND.value(), "Investment not found", e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(InvestmentDuplicateException.class)
    public ResponseEntity<BadRequestExceptionDetails> handleDuplicateException(InvestmentDuplicateException e, WebRequest webRequest) {
        BadRequestExceptionDetails errorResponse = new BadRequestExceptionDetails(
                HttpStatus.CONFLICT.value(), "Investment duplicate", e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }


}
