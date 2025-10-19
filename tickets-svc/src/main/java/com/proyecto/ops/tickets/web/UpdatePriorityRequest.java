/*
 * -----------------------------------------------------------------------------
 * UpdatePriorityRequest.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   DTO (Data Transfer Object) utilizado para actualizar la prioridad de un ticket
 *   dentro del microservicio "tickets-svc".
 *
 * Contexto de uso:
 *   - Se recibe en el endpoint PATCH /tickets/{id}/priority del controlador
 *     {@link com.proyecto.ops.tickets.web.TicketController}.
 *   - Permite modificar únicamente el campo de prioridad de un ticket existente.
 *
 * Diseño:
 *   - Implementado como un record inmutable de Java.
 *   - Utiliza la validación @NotNull para garantizar que se envíe un valor válido.
 *   - El campo `priority` utiliza el enum {@link com.proyecto.ops.tickets.model.TicketPriority},
 *     que define los niveles LOW, MEDIUM y HIGH.
 *
 * Mantenibilidad:
 *   - Si en el futuro se agregan más atributos editables (como estado o asignado a),
 *     se recomienda crear DTOs independientes para cada operación.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.tickets.web;

import com.proyecto.ops.tickets.model.TicketPriority;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de solicitud utilizado para actualizar la prioridad de un ticket existente.
 */
public record UpdatePriorityRequest(
        // Nueva prioridad que se asignará al ticket (LOW, MEDIUM o HIGH). Campo obligatorio.
        @NotNull TicketPriority priority
) {}