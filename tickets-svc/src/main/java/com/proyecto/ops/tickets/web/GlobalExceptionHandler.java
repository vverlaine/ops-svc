/*
 * -----------------------------------------------------------------------------
 * GlobalExceptionHandler.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Manejador global de excepciones para el microservicio "tickets-svc".
 *   Centraliza el tratamiento de errores comunes que pueden ocurrir en la capa web
 *   (controladores) y devuelve respuestas HTTP estructuradas y coherentes.
 *
 * Contexto de uso:
 *   - Anotada con @ControllerAdvice para que Spring la aplique automáticamente
 *     a todos los controladores REST del servicio.
 *   - Captura excepciones específicas como:
 *       • MethodArgumentNotValidException → Errores de validación (anotaciones @Valid).
 *       • DataIntegrityViolationException → Violaciones de integridad referencial o duplicados.
 *
 * Diseño:
 *   - Utiliza ResponseEntity para construir respuestas HTTP con el código y cuerpo adecuados.
 *   - Devuelve objetos Map sencillos con detalles del error en formato JSON.
 *   - Mantiene separación entre la lógica del negocio y el manejo de errores.
 *
 * Mantenibilidad:
 *   - Se pueden agregar nuevos métodos @ExceptionHandler para manejar errores específicos.
 *   - Ideal para centralizar logs o notificaciones de errores en el futuro.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.tickets.web;

import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Manejador global de excepciones para los controladores REST del microservicio "tickets-svc".
 *
 * Captura errores comunes y devuelve respuestas HTTP consistentes.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja las excepciones de validación generadas por anotaciones @Valid.
     *
     * @param ex Excepción que contiene los errores de validación de campos.
     * @return Respuesta HTTP 400 (Bad Request) con los campos inválidos y sus mensajes de error.
     */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        // Extrae los errores de validación de los campos y los convierte en un mapa campo → mensaje.
        var errors = ex.getBindingResult().getFieldErrors().stream()
            .collect(java.util.stream.Collectors.toMap(
                fe -> fe.getField(),
                fe -> fe.getDefaultMessage(),
                (a, b) -> a
            ));
        // Devuelve una respuesta con código 400 y el detalle de los campos inválidos.
        return ResponseEntity.badRequest().body(Map.of(
            "error", "validation_error",
            "fields", errors
        ));
  }

    /**
     * Maneja las excepciones de violación de integridad referencial o restricciones únicas.
     *
     * @param ex Excepción lanzada cuando se infringe una restricción de la base de datos.
     * @return Respuesta HTTP 409 (Conflict) con el detalle del error.
     */
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<?> handleIntegrity(DataIntegrityViolationException ex) {
        // Devuelve una respuesta con código 409 (conflicto) e incluye el mensaje más específico del error.
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
            "error", "data_integrity_violation",
            "message", ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage()
        ));
  }
}