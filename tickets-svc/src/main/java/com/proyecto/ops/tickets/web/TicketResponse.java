/*
 * -----------------------------------------------------------------------------
 * TicketResponse.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   DTO (Data Transfer Object) utilizado para representar los datos de un ticket
 *   en las respuestas HTTP del microservicio "tickets-svc".
 *
 * Contexto de uso:
 *   - Devuelto por los endpoints del controlador {@link com.proyecto.ops.tickets.web.TicketController}.
 *   - Incluye información combinada del ticket, cliente, solicitante y metadatos
 *     de creación.
 *
 * Diseño:
 *   - Implementado como un record inmutable de Java.
 *   - Contiene campos que representan tanto la entidad base (Ticket) como datos
 *     complementarios obtenidos de otros servicios.
 *
 * Campos:
 *   id               → Identificador único del ticket.
 *   title            → Título o resumen del ticket.
 *   description      → Descripción detallada del ticket.
 *   status           → Estado actual del ticket (OPEN, IN_PROGRESS, CLOSED).
 *   priority         → Prioridad del ticket (LOW, MEDIUM, HIGH).
 *   customerId       → Identificador del cliente asociado.
 *   customerName     → Nombre del cliente (consultado en customers-svc).
 *   siteId           → Identificador del sitio (si aplica).
 *   assetId          → Identificador del activo (si aplica).
 *   requestedBy      → Usuario que solicitó el ticket.
 *   requestedByName  → Nombre del usuario solicitante (consultado en contacts-svc).
 *   createdBy        → Usuario que registró el ticket.
 *   createdAt        → Fecha y hora en que se creó el ticket.
 *
 * Mantenibilidad:
 *   - Si se agregan nuevos campos a la entidad Ticket o se incluyen datos de
 *     otros microservicios, deben reflejarse aquí y en el método `toResponse()`.
 * -----------------------------------------------------------------------------
 */
// tickets-svc/src/main/java/com/proyecto/ops/tickets/web/TicketResponse.java
package com.proyecto.ops.tickets.web;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO de respuesta que representa un ticket completo con información
 * relacionada del cliente y solicitante.
 */
public record TicketResponse(
        // Identificador único del ticket.
        UUID id,
        // Título o nombre del ticket.
        String title,
        // Descripción detallada del ticket.
        String description,
        // Estado actual del ticket (OPEN, IN_PROGRESS, CLOSED).
        String status,
        // Nivel de prioridad del ticket (LOW, MEDIUM, HIGH).
        String priority,
        // Identificador del cliente asociado al ticket.
        UUID customerId,
        // Nombre del cliente asociado (consultado en customers-svc).
        String customerName,
        // Identificador del sitio asociado (si aplica).
        UUID siteId,
        // Identificador del activo relacionado (si aplica).
        UUID assetId,
        // Identificador del usuario que solicitó el ticket (si aplica).
        UUID requestedBy,
        // Nombre del usuario solicitante (obtenido del servicio contacts-svc).
        String requestedByName,
        // Usuario que creó o registró el ticket.
        String createdBy,
        // Fecha y hora en que se creó el ticket.
        OffsetDateTime createdAt
) {}