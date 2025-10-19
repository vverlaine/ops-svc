/*
 * -----------------------------------------------------------------------------
 * VisitPriority.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Define los niveles de prioridad asignables a una visita dentro del
 *   microservicio "visits-svc".
 *
 * Contexto de uso:
 *   - Este enumerado permite clasificar las visitas según su urgencia o importancia.
 *   - Es utilizado por la entidad {@link com.visits.model.Visit} para establecer
 *     la prioridad de cada registro.
 *
 * Valores posibles:
 *   • LOW     → Visita de baja prioridad o sin urgencia inmediata.
 *   • MEDIUM  → Visita estándar, programada dentro del flujo normal.
 *   • HIGH    → Visita de alta prioridad o atención urgente.
 *
 * Mantenibilidad:
 *   - Si se agregan nuevas prioridades, debe actualizarse cualquier lógica
 *     de ordenamiento, validación o visualización que dependa de este enum.
 * -----------------------------------------------------------------------------
 */
package com.visits.model;

/**
 * Enumeración que define los niveles de prioridad de una visita.
 */
public enum VisitPriority {
    // Baja prioridad: tareas sin urgencia inmediata.
    LOW,
    // Prioridad media: visitas regulares planificadas.
    MEDIUM,
    // Alta prioridad: visitas urgentes o críticas.
    HIGH
}
