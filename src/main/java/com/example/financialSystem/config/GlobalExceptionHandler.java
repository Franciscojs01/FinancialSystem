package com.example.financialSystem.config;

import com.example.financialSystem.exceptions.InvestmentDuplicateException;
import com.example.financialSystem.exceptions.InvestmentNotFoundException;
import com.example.financialSystem.exceptions.UserDuplicateException;
import com.example.financialSystem.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<RestErrorMessage> handleUserNotFoundException(UserNotFoundException e, WebRequest webRequest) {
        RestErrorMessage errorResponse = new RestErrorMessage(
                HttpStatus.NOT_FOUND.value(), "User not found", e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(UserDuplicateException.class)
    public ResponseEntity<RestErrorMessage> handleUserDuplicateException(UserDuplicateException e, WebRequest webRequest) {
        RestErrorMessage errorResponse = new RestErrorMessage(
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
                .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                .body("An unexpected error occurred: " + ex.getMessage());
    }

    @ExceptionHandler(InvestmentNotFoundException.class)
    public ResponseEntity<RestErrorMessage> handleInvestmentNotFoundException(InvestmentNotFoundException e, WebRequest webRequest) {
        RestErrorMessage errorResponse = new RestErrorMessage(
                HttpStatus.NOT_FOUND.value(), "Investment not found", e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(InvestmentDuplicateException.class)
    public ResponseEntity<RestErrorMessage> handleDuplicateException(InvestmentDuplicateException e, WebRequest webRequest) {
        RestErrorMessage errorResponse = new RestErrorMessage(
                HttpStatus.CONFLICT.value(), "Investment duplicate", e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }


}
