package com.example.financialSystem.handler;

import com.example.financialSystem.exception.AlreadyExistsException;
import com.example.financialSystem.exception.ExceptionDetails;
import com.example.financialSystem.exception.FinancialException;
import com.example.financialSystem.exception.ValidationExceptionDetails;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

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
                .stream().map(fe -> fe.getField()).collect(Collectors.joining(", "));

        String fieldsMessage = ex.getBindingResult().getFieldErrors()
                .stream().map(fe -> fe.getDefaultMessage()).collect(Collectors.joining(", "));

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
