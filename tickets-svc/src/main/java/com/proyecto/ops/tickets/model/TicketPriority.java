/*
 * -----------------------------------------------------------------------------
 * TicketPriority.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Define los niveles de prioridad que puede tener un ticket dentro del sistema.
 *
 * Contexto de uso:
 *   - Pertenece al microservicio "tickets-svc".
 *   - Es utilizado por la entidad {@link com.proyecto.ops.tickets.model.Ticket}
 *     para indicar la urgencia o relevancia de una solicitud.
 *
 * Valores posibles:
 *   • LOW    → Baja prioridad. El ticket puede resolverse sin urgencia.
 *   • MEDIUM → Prioridad media. Requiere atención oportuna.
 *   • HIGH   → Alta prioridad. Debe resolverse con carácter urgente.
 *
 * Mantenibilidad:
 *   - Si se agregan nuevos niveles de prioridad (por ejemplo, CRITICAL o URGENT),
 *     deben actualizarse los controladores, la lógica de negocio y las vistas
 *     que dependen de este enumerado.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.tickets.model;

/**
 * Enumeración que representa los diferentes niveles de prioridad
 * asignables a un ticket dentro del sistema.
 */
public enum TicketPriority {
    // Prioridad baja: el ticket puede ser atendido sin urgencia.
    LOW,
    // Prioridad media: requiere atención dentro de un tiempo razonable.
    MEDIUM,
    // Prioridad alta: debe ser atendido de inmediato o con máxima prioridad.
    HIGH
}