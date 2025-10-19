/*
 * -----------------------------------------------------------------------------
 * VisitEmailService.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Define la interfaz de servicio responsable de manejar los correos electrónicos
 *   relacionados con las visitas dentro del microservicio "visits-svc".
 *
 * Contexto de uso:
 *   - Implementada por clases de servicio que gestionan el envío de correos
 *     cuando ocurre un evento relevante en una visita.
 *   - Su método principal `onVisitCompleted(Visit visit)` se ejecuta al completarse
 *     una visita, permitiendo notificar al cliente o registrar la acción.
 *
 * Diseño:
 *   - Es una interfaz funcional, lo que facilita su inyección e implementación
 *     en diferentes contextos (por ejemplo, envío real de correo o modo simulado).
 *
 * Mantenibilidad:
 *   - Si se agregan nuevos eventos relacionados con correos (por ejemplo, visita cancelada),
 *     deben incluirse nuevos métodos en esta interfaz.
 * -----------------------------------------------------------------------------
 */
package com.visits.service;

import com.visits.model.Visit;

/**
 * Interfaz de servicio encargada de manejar el envío de correos relacionados con las visitas.
 */
public interface VisitEmailService {
    /**
     * Envía o gestiona el correo electrónico correspondiente cuando una visita ha sido completada.
     *
     * @param visit Objeto de la visita que ha sido completada.
     */
    void onVisitCompleted(Visit visit);
}