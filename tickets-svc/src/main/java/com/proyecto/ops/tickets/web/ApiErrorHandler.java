package com.proyecto.ops.tickets.web;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class ApiErrorHandler {

    // Respuestas que ya lanzas con ResponseStatusException
    @ExceptionHandler(ResponseStatusException.class)
    public ProblemDetail handle(ResponseStatusException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(ex.getStatusCode(), ex.getReason());
        pd.setTitle("Request failed");
        pd.setProperty("timestamp", OffsetDateTime.now().toString());
        return pd;
    }

    // Validaciones @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handle(MethodArgumentNotValidException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation failed");
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.toList());
        pd.setDetail("Fix the validation errors and retry.");
        pd.setProperty("errors", errors);
        pd.setProperty("timestamp", OffsetDateTime.now().toString());
        return pd;
    }

    // Cualquier otro error “no mapeado”
    @ExceptionHandler(Exception.class)
    public ProblemDetail handle(Exception ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setTitle("Unexpected error");
        pd.setDetail("Something went wrong. Contact support if it persists.");
        pd.setProperty("timestamp", OffsetDateTime.now().toString());
        return pd;
    }
}