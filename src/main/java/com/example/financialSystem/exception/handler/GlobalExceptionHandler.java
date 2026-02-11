package com.example.financialSystem.exception.handler;

import com.example.financialSystem.exception.*;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NoChangeDetectedException.class)
    public ResponseEntity<ExceptionDetails> handleNoChangeDetectedException(NoChangeDetectedException ex) {
        ExceptionDetails body = ExceptionDetails.builder()
                .timestamp(LocalDateTime.now())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .title("No Change Detected")
                .details(ex.getMessage())
                .developerMessage(ex.getClass().getName())
                .build();

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionDetails> handleAccessDeniedException(AccessDeniedException ex) {
        ExceptionDetails body = ExceptionDetails.builder()
                .timestamp(LocalDateTime.now())
                .statusCode(HttpStatus.FORBIDDEN.value())
                .title("Access Denied")
                .details(ex.getMessage())
                .developerMessage(ex.getClass().getName())
                .build();

        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(FinancialException.class)
    public ResponseEntity<ExceptionDetails> handleFinancialExceptions(FinancialException ex) {
        ExceptionDetails body = ExceptionDetails.builder()
                .timestamp(LocalDateTime.now())
                .statusCode(HttpStatus.NOT_FOUND.value())
                .title("Resource Not Found")
                .details(ex.getMessage())
                .developerMessage(ex.getClass().getName())
                .build();

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ExceptionDetails> handleAlreadyExists(AlreadyExistsException ex) {

        ExceptionDetails body = ExceptionDetails.builder()
                .timestamp(LocalDateTime.now())
                .statusCode(HttpStatus.CONFLICT.value())
                .title("Resource Already Exists")
                .details(ex.getMessage())
                .developerMessage(ex.getClass().getName())
                .build();

        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        String fields = ex.getBindingResult().getFieldErrors()
                .stream().map(FieldError::getField).collect(Collectors.joining(", "));

        String fieldsMessage = ex.getBindingResult().getFieldErrors()
                .stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(", "));

        ValidationExceptionDetails body = ValidationExceptionDetails.builder()
                .timestamp(LocalDateTime.now())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .title("Invalid Request Fields")
                .details("Some fields are invalid")
                .fields(fields)
                .fieldsMessage(fieldsMessage)
                .developerMessage(ex.getClass().getName())
                .build();

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            Object body,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        ExceptionDetails details = ExceptionDetails.builder()
                .timestamp(LocalDateTime.now())
                .statusCode(status.value())
                .title("Internal Error")
                .details(ex.getMessage())
                .developerMessage(ex.getClass().getName())
                .build();

        return new ResponseEntity<>(details, headers, status);
    }
}
