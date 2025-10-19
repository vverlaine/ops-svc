/*
 * -----------------------------------------------------------------------------
 * UpdateStatusRequest.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Representa el objeto de solicitud (DTO) utilizado para actualizar el estado
 *   de una orden de trabajo dentro del microservicio "work-orders-svc".
 *
 * Contexto de uso:
 *   - Este record se utiliza en el endpoint correspondiente del controlador REST
 *     cuando un técnico o supervisor cambia el estado de una orden (por ejemplo:
 *     de ASSIGNED a STARTED, o de STARTED a DONE).
 *   - Se recibe en formato JSON y es validado automáticamente mediante Jakarta Validation.
 *
 * Diseño:
 *   - Implementado como un `record` inmutable para simplificar el manejo de datos.
 *   - Incluye dos campos:
 *       • status → El nuevo estado que se desea asignar (obligatorio).
 *       • notes  → Comentarios opcionales relacionados con el cambio de estado.
 *
 * Mantenibilidad:
 *   - Si en el futuro se agregan razones o metadatos al cambio de estado,
 *     estos pueden añadirse como nuevos campos sin afectar la compatibilidad.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.workorders.web;

import com.proyecto.ops.workorders.model.WoStatus;

import jakarta.validation.constraints.NotNull;

/**
 * DTO (Data Transfer Object) para la actualización del estado de una orden de trabajo.
 *
 * @param status Nuevo estado que se desea asignar (obligatorio, validado con @NotNull).
 * @param notes  Comentarios o notas adicionales sobre el cambio de estado (opcional).
 */
// Record inmutable utilizado como DTO para recibir solicitudes de cambio de estado.
// El campo 'status' es obligatorio y determina el nuevo estado de la orden.
// El campo 'notes' es opcional y permite adjuntar comentarios relacionados con el cambio.
public record UpdateStatusRequest(@NotNull WoStatus status, String notes) {}