/*
 * -----------------------------------------------------------------------------
 * VisitState.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Define los posibles estados que puede tener una visita dentro del ciclo de vida
 *   gestionado por el microservicio "visits-svc".
 *
 * Contexto de uso:
 *   - Se utiliza en la entidad {@link com.visits.model.Visit} para reflejar el
 *     progreso de una visita desde su planificación hasta su finalización.
 *   - Permite controlar las transiciones entre estados mediante reglas de negocio
 *     (por ejemplo, solo se puede pasar de PLANNED a STARTED o CANCELLED).
 *
 * Valores posibles:
 *   • PLANNED   → La visita ha sido programada, pero aún no ha iniciado.
 *   • STARTED   → La visita ha iniciado (check-in realizado por el técnico).
 *   • DONE      → La visita fue completada exitosamente.
 *   • CANCELLED → La visita fue cancelada antes de comenzar.
 *   • NO_SHOW   → El cliente o técnico no se presentó (visita no realizada).
 *
 * Mantenibilidad:
 *   - Si se agregan nuevos estados, deben actualizarse las reglas de transición
 *     en la clase Visit y en cualquier lógica de negocio que los evalúe.
 * -----------------------------------------------------------------------------
 */
package com.visits.model;

/**
 * Enumeración que define los estados del ciclo de vida de una visita.
 */
public enum VisitState {
    // Estado inicial: la visita está planificada pero aún no ha comenzado.
    PLANNED,
    // Estado cuando el técnico realiza el check-in e inicia la visita.
    STARTED,
    // Estado final exitoso: la visita fue completada.
    DONE,
    // Estado cuando la visita fue cancelada antes de iniciar.
    CANCELLED,
    // Estado cuando la visita no se realizó porque el cliente o técnico no asistió.
    NO_SHOW
}
