/*
 * -----------------------------------------------------------------------------
 * TicketDtos.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Contiene los Data Transfer Objects (DTOs) utilizados por el controlador
 *   {@link com.proyecto.ops.tickets.web.TicketController} para crear y actualizar
 *   tickets en el microservicio "tickets-svc".
 *
 * Contexto de uso:
 *   - Define los modelos de datos que representan las solicitudes entrantes (Request Bodies)
 *     del API REST.
 *   - Separa la capa de presentación (controladores) de la capa de persistencia (entidades JPA).
 *
 * Diseño:
 *   - Implementado como clase contenedora `TicketDtos` que agrupa los records internos:
 *       • CreateReq → Datos requeridos para crear un nuevo ticket.
 *       • UpdateStatusReq → Datos para actualizar el estado de un ticket existente.
 *   - Usa anotaciones de validación de Jakarta (jakarta.validation.constraints):
 *       @NotBlank  → Indica que un campo no puede ser nulo ni vacío.
 *       @Pattern   → Restringe los valores posibles de ciertos campos a opciones válidas.
 *
 * Mantenibilidad:
 *   - Si se agregan nuevos campos o tipos de actualización, deben añadirse nuevos records
 *     dentro de esta clase, siguiendo el mismo patrón.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.tickets.web;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Clase contenedora que agrupa los DTOs de solicitud relacionados con la entidad Ticket.
 */
public class TicketDtos {

    /**
     * DTO utilizado para la creación de un nuevo ticket.
     *
     * Incluye validaciones para los campos obligatorios y restricciones en los valores de estado y prioridad.
     */
    public record CreateReq(
            // Título del ticket (obligatorio, no puede estar vacío).
            @NotBlank String title,
            // Descripción detallada del ticket (opcional).
            String description,
            // Estado inicial del ticket (solo puede ser OPEN, IN_PROGRESS o CLOSED).
            @Pattern(regexp = "OPEN|IN_PROGRESS|CLOSED") String status,
            // Nivel de prioridad del ticket (solo puede ser LOW, MEDIUM o HIGH).
            @Pattern(regexp = "LOW|MEDIUM|HIGH") String priority,
            // Identificador del cliente asociado al ticket.
            UUID customerId,
            // Identificador del activo relacionado con el ticket (opcional).
            UUID assetId,
            // Usuario responsable de la creación del ticket (obligatorio).
            @NotBlank String createdBy
    ) {}

    /**
     * DTO utilizado para actualizar el estado de un ticket existente.
     *
     * Incluye validación para restringir los valores posibles del campo "status".
     */
    public record UpdateStatusReq(
            // Nuevo estado del ticket (debe ser OPEN, IN_PROGRESS o CLOSED).
            @Pattern(regexp = "OPEN|IN_PROGRESS|CLOSED") String status
    ) {}
}