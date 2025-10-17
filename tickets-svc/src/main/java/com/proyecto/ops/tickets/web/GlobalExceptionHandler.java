package com.proyecto.ops.tickets.web;

import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
    var errors = ex.getBindingResult().getFieldErrors().stream()
      .collect(java.util.stream.Collectors.toMap(
        fe -> fe.getField(),
        fe -> fe.getDefaultMessage(),
        (a,b) -> a
      ));
    return ResponseEntity.badRequest().body(Map.of(
      "error", "validation_error",
      "fields", errors
    ));
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<?> handleIntegrity(DataIntegrityViolationException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
      "error", "data_integrity_violation",
      "message", ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage()
    ));
  }
}