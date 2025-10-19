/*
 * -----------------------------------------------------------------------------
 * TicketStatus.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Define los diferentes estados posibles que puede tener un ticket dentro del sistema.
 *
 * Contexto de uso:
 *   - Pertenece al microservicio "tickets-svc".
 *   - Es utilizado por la entidad {@link com.proyecto.ops.tickets.model.Ticket}
 *     para representar el ciclo de vida de un ticket.
 *
 * Valores posibles:
 *   • OPEN         → El ticket ha sido creado y está pendiente de atención.
 *   • IN_PROGRESS  → El ticket está siendo atendido o trabajado.
 *   • CLOSED       → El ticket ha sido resuelto y cerrado.
 *
 * Mantenibilidad:
 *   - Si se agregan nuevos estados (por ejemplo, REOPENED o ON_HOLD),
 *     deben reflejarse en la lógica de negocio y en los controladores que manejan tickets.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.tickets.model;

/**
 * Enumeración que representa los posibles estados de un ticket
 * durante su ciclo de vida.
 */
public enum TicketStatus {
    // Ticket recién creado y pendiente de atención.
    OPEN,
    // Ticket que se encuentra actualmente en proceso de resolución.
    IN_PROGRESS,
    // Ticket que ha sido resuelto y cerrado definitivamente.
    CLOSED
}