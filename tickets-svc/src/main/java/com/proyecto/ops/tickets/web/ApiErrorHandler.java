/*
 * -----------------------------------------------------------------------------
 * ApiErrorHandler.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Clase global de manejo de excepciones (error handler) para el microservicio "tickets-svc".
 *   Centraliza el tratamiento de errores y transforma las excepciones en respuestas HTTP
 *   con formato uniforme mediante el objeto ProblemDetail.
 *
 * Contexto de uso:
 *   - Anotada con @ControllerAdvice para que Spring la aplique a todos los controladores.
 *   - Captura diferentes tipos de excepciones y genera respuestas coherentes y estructuradas.
 *
 * Diseño:
 *   - Usa el tipo ProblemDetail (Spring 6) para devolver detalles estándar de error.
 *   - Incluye tres manejadores:
 *       • ResponseStatusException → Errores personalizados con estado HTTP definido.
 *       • MethodArgumentNotValidException → Errores de validación de datos (@Valid).
 *       • Exception → Cualquier error no mapeado (genérico).
 *   - En cada caso, se agrega una marca de tiempo (timestamp) para facilitar el debugging.
 *
 * Mantenibilidad:
 *   - Se pueden agregar nuevos métodos @ExceptionHandler para manejar excepciones específicas
 *     como DataIntegrityViolationException, HttpMessageNotReadableException, etc.
 * -----------------------------------------------------------------------------
 */
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

/**
 * Manejador global de errores para los controladores REST del microservicio "tickets-svc".
 *
 * Proporciona respuestas estandarizadas en formato ProblemDetail para diferentes tipos de excepciones.
 */
@ControllerAdvice
public class ApiErrorHandler {

    /**
     * Maneja excepciones del tipo ResponseStatusException.
     *
     * @param ex Excepción lanzada con un estado HTTP específico.
     * @return Objeto ProblemDetail con el estado y detalle del error.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ProblemDetail handle(ResponseStatusException ex) {
        // Crea un objeto ProblemDetail con el código de estado y motivo definidos en la excepción.
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(ex.getStatusCode(), ex.getReason());
        pd.setTitle("Request failed");
        pd.setProperty("timestamp", OffsetDateTime.now().toString());
        return pd;
    }

    /**
     * Maneja errores de validación provenientes de anotaciones @Valid.
     *
     * @param ex Excepción que contiene los errores de validación.
     * @return Objeto ProblemDetail con lista de errores de campo y mensaje de corrección.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handle(MethodArgumentNotValidException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation failed");
        // Recolecta todos los errores de validación de los campos y los formatea como lista de texto.
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

    /**
     * Maneja cualquier otra excepción no controlada (error genérico).
     *
     * @param ex Excepción inesperada.
     * @return Objeto ProblemDetail con estado 500 y mensaje genérico de error.
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handle(Exception ex) {
        // Crea una respuesta genérica de error con estado HTTP 500 (Internal Server Error).
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setTitle("Unexpected error");
        pd.setDetail("Something went wrong. Contact support if it persists.");
        pd.setProperty("timestamp", OffsetDateTime.now().toString());
        return pd;
    }
}