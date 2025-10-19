/*
 * -----------------------------------------------------------------------------
 * CreateWorkOrderRequest.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Representa el objeto de solicitud (DTO) utilizado para crear una nueva
 *   orden de trabajo dentro del microservicio "work-orders-svc".
 *
 * Contexto de uso:
 *   - Este record se recibe en formato JSON desde el cliente mediante una
 *     petición POST al endpoint correspondiente del controlador REST.
 *   - Contiene la información necesaria para generar una nueva orden,
 *     incluyendo el ticket asociado, el técnico, la fecha de programación
 *     y notas adicionales.
 *
 * Diseño:
 *   - Implementado como un `record`, lo que lo hace inmutable y eficiente.
 *   - Incluye anotaciones de validación (@NotNull) para asegurar la integridad
 *     de los datos recibidos.
 *
 * Mantenibilidad:
 *   - Si se agregan nuevos campos (por ejemplo, prioridad o tipo de orden),
 *     se pueden incluir fácilmente dentro del record sin afectar las peticiones existentes.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.workorders.web;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

/**
 * DTO (Data Transfer Object) utilizado para la creación de nuevas órdenes de trabajo.
 *
 * @param ticketId Identificador único del ticket asociado (obligatorio).
 * @param technicianId Identificador del técnico asignado (opcional).
 * @param scheduledAt Fecha y hora programadas para ejecutar la orden (opcional).
 * @param notes Notas o comentarios adicionales sobre la orden (opcional).
 */
public record CreateWorkOrderRequest(
    // Identificador único del ticket relacionado con la orden de trabajo.
    // Este campo es obligatorio y debe estar presente en la solicitud.
    @NotNull UUID ticketId,

    // Identificador del técnico asignado a la orden. Puede ser nulo si aún no se ha asignado.
    UUID technicianId,

    // Fecha y hora programadas para la ejecución de la orden. Opcional.
    OffsetDateTime scheduledAt,

    // Notas o comentarios adicionales proporcionados por el usuario. Opcional.
    String notes
) {}