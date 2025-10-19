/*
 * -----------------------------------------------------------------------------
 * GlobalExceptionHandler.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Clase encargada del manejo global de excepciones dentro del microservicio
 *   "contacts-svc". Centraliza la captura de errores de validación y genera
 *   respuestas HTTP estandarizadas.
 *
 * Contexto de uso:
 *   - Aplicable a todos los controladores (@RestController) del módulo.
 *   - Se encarga de interceptar excepciones lanzadas durante la validación de datos
 *     en los endpoints (por ejemplo, errores en CreateContactRequest).
 *
 * Diseño:
 *   - Anotada con @ControllerAdvice para aplicar de forma transversal a todo el API.
 *   - El método handleValidation() captura tanto:
 *       • MethodArgumentNotValidException → Errores de validación en @RequestBody.
 *       • ConstraintViolationException → Errores de validación en @RequestParam o @PathVariable.
 *
 * Comportamiento:
 *   - Devuelve un cuerpo JSON con:
 *       timestamp → Fecha/hora del error.
 *       status    → Código HTTP (400).
 *       error     → Descripción genérica ("Bad Request").
 *       message   → Mensaje de validación fallida.
 *       path      → URI que provocó el error.
 *
 * Mantenibilidad:
 *   - Se puede extender para manejar más tipos de excepciones (por ejemplo, EntityNotFoundException).
 *   - Mejora la consistencia de las respuestas de error entre controladores.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.contacts.web;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

/**
 * Manejador global de excepciones para todos los controladores del microservicio.
 *
 * Permite capturar y responder de forma uniforme ante errores de validación.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja errores de validación tanto en cuerpos de solicitud (@RequestBody)
     * como en parámetros (@RequestParam o @PathVariable).
     *
     * @param ex  Excepción capturada (MethodArgumentNotValidException o ConstraintViolationException).
     * @param req Objeto HttpServletRequest con información de la solicitud original.
     * @return Respuesta HTTP 400 (Bad Request) con detalles del error en formato JSON.
     */
    @ExceptionHandler({ MethodArgumentNotValidException.class, ConstraintViolationException.class })
    public ResponseEntity<Map<String, Object>> handleValidation(Exception ex, HttpServletRequest req) {
        // Crea un mapa ordenado para construir la respuesta JSON de error.
        Map<String, Object> body = new LinkedHashMap<>();
        // Agrega la marca de tiempo actual en formato ISO.
        body.put("timestamp", OffsetDateTime.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", "Validación fallida");
        body.put("path", req.getRequestURI());
        // Devuelve la respuesta HTTP con código 400 y el cuerpo estructurado.
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}