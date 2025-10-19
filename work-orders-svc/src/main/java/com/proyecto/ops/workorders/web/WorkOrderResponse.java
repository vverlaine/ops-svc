/*
 * -----------------------------------------------------------------------------
 * WorkOrderResponse.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Representa el objeto de respuesta (DTO) que se devuelve al cliente
 *   cuando se consulta, crea o actualiza una orden de trabajo dentro del
 *   microservicio "work-orders-svc".
 *
 * Contexto de uso:
 *   - Es utilizado por el controlador REST {@link com.proyecto.ops.workorders.web.WorkOrderController}.
 *   - Se genera a partir de la entidad {@link com.proyecto.ops.workorders.model.WorkOrder}.
 *   - Se devuelve en las respuestas HTTP para exponer únicamente los datos relevantes
 *     de la orden de trabajo sin incluir detalles internos del modelo o relaciones.
 *
 * Diseño:
 *   - Implementado como un `record`, lo que lo hace inmutable y más seguro para transferencia.
 *   - Incluye campos clave como fechas de programación, inicio, finalización y estado.
 *
 * Mantenibilidad:
 *   - Si la entidad WorkOrder se amplía con nuevos campos que deban ser visibles
 *     para el cliente, estos deben agregarse también a este DTO.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.workorders.web;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.proyecto.ops.workorders.model.WoStatus;

/**
 * DTO (Data Transfer Object) de respuesta para representar una orden de trabajo.
 *
 * @param id Identificador único de la orden de trabajo.
 * @param ticketId Identificador del ticket asociado.
 * @param technicianId Identificador del técnico asignado.
 * @param status Estado actual de la orden (PENDING, ASSIGNED, STARTED, etc.).
 * @param scheduledAt Fecha y hora programadas para ejecución.
 * @param startedAt Fecha y hora reales de inicio.
 * @param endedAt Fecha y hora reales de finalización.
 * @param notes Notas o comentarios asociados a la orden.
 * @param createdAt Fecha y hora de creación del registro.
 */
public record WorkOrderResponse(
    UUID id,
    UUID ticketId,
    UUID technicianId,
    WoStatus status,
    OffsetDateTime scheduledAt,
    OffsetDateTime startedAt,
    OffsetDateTime endedAt,
    String notes,
    OffsetDateTime createdAt
) {}