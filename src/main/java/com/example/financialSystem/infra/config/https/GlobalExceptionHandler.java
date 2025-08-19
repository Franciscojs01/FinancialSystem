package com.example.financialSystem.infra.config.https;

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
}
