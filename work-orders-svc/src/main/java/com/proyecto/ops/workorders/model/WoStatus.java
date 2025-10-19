/*
 * -----------------------------------------------------------------------------
 * WoStatus.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Enumeración que define los posibles estados de una orden de trabajo (Work Order)
 *   dentro del microservicio "work-orders-svc".
 *
 * Contexto de uso:
 *   - Utilizada por la entidad {@link com.proyecto.ops.workorders.model.WorkOrder}.
 *   - Permite controlar y validar el ciclo de vida de una orden técnica, desde su
 *     creación hasta su cierre o cancelación.
 *
 * Estados definidos:
 *   • PENDING   → La orden ha sido creada, pero aún no está asignada a un técnico.
 *   • ASSIGNED  → La orden ha sido asignada a un técnico, pero aún no ha comenzado.
 *   • STARTED   → El técnico ha iniciado la ejecución de la orden.
 *   • PAUSED    → La orden fue iniciada, pero se encuentra temporalmente detenida.
 *   • DONE      → La orden fue completada exitosamente.
 *   • CANCELLED → La orden fue cancelada antes o durante su ejecución.
 *
 * Mantenibilidad:
 *   - Se pueden agregar nuevos estados según evolucione el flujo operativo
 *     (por ejemplo, "REVIEWED" o "ESCALATED").
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.workorders.model;

/**
 * Enumeración que representa los diferentes estados posibles de una orden de trabajo.
 */
public enum WoStatus {
  PENDING,   // Orden creada pero aún no asignada a un técnico.
  ASSIGNED,  // Orden asignada a un técnico, pendiente de inicio.
  STARTED,   // Orden actualmente en ejecución.
  PAUSED,    // Orden en pausa temporal.
  DONE,      // Orden finalizada exitosamente.
  CANCELLED  // Orden cancelada antes o durante la ejecución.
}