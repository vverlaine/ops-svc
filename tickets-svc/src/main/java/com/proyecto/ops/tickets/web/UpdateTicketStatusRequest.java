/*
 * -----------------------------------------------------------------------------
 * UpdateTicketStatusRequest.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   DTO (Data Transfer Object) utilizado para actualizar el estado de un ticket
 *   dentro del microservicio "tickets-svc".
 *
 * Contexto de uso:
 *   - Se recibe en el endpoint PATCH /tickets/{id}/status del controlador
 *     {@link com.proyecto.ops.tickets.web.TicketController}.
 *   - Permite modificar únicamente el campo de estado de un ticket existente.
 *
 * Diseño:
 *   - Implementado como un record inmutable de Java.
 *   - Utiliza la validación @NotNull para asegurar que siempre se reciba un valor.
 *   - El campo `status` utiliza el enum {@link com.proyecto.ops.tickets.model.TicketStatus},
 *     que define los estados OPEN, IN_PROGRESS y CLOSED.
 *
 * Mantenibilidad:
 *   - Si en el futuro se agregan más atributos editables del ticket (como prioridad o asignado a),
 *     se recomienda crear DTOs específicos para cada operación.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.tickets.web;

import com.proyecto.ops.tickets.model.TicketStatus;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de solicitud utilizado para actualizar el estado de un ticket existente.
 */
public record UpdateTicketStatusRequest(
        // Nuevo estado que se asignará al ticket (OPEN, IN_PROGRESS o CLOSED). Campo obligatorio.
        @NotNull TicketStatus status
) {}