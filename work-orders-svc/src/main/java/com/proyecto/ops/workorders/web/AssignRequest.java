/*
 * -----------------------------------------------------------------------------
 * AssignRequest.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Representa el objeto de solicitud (DTO) utilizado para asignar un técnico
 *   a una orden de trabajo dentro del microservicio "work-orders-svc".
 *
 * Contexto de uso:
 *   - Este record se utiliza en el endpoint correspondiente del controlador REST
 *     encargado de manejar las asignaciones de órdenes de trabajo.
 *   - Se recibe en formato JSON desde el cliente y se valida automáticamente
 *     mediante anotaciones de Jakarta Validation.
 *
 * Diseño:
 *   - Implementado como un `record`, lo que lo hace inmutable y más eficiente.
 *   - Contiene un solo campo obligatorio: technicianId.
 *   - Usa la anotación @NotNull para asegurar que el identificador del técnico
 *     siempre esté presente en la solicitud.
 *
 * Mantenibilidad:
 *   - Si en el futuro se requiere agregar más campos (por ejemplo, fecha de asignación
 *     o comentarios), pueden incluirse directamente en el record.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.workorders.web;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

/**
 * DTO (Data Transfer Object) para la solicitud de asignación de técnico a una orden de trabajo.
 *
 * @param technicianId Identificador único del técnico a asignar (no puede ser nulo).
 */
public record AssignRequest(@NotNull UUID technicianId) {}