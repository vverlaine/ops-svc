/*
 * -----------------------------------------------------------------------------
 * NoteVisibility.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Define los niveles de visibilidad que puede tener una nota asociada a una visita
 *   dentro del microservicio "visits-svc".
 *
 * Contexto de uso:
 *   - Este enumerado se utiliza para indicar quién puede visualizar una nota
 *     registrada por un técnico o supervisor en una visita.
 *   - Los valores determinan si la nota es de uso interno o visible para el cliente.
 *
 * Valores posibles:
 *   • INTERNAL → La nota solo puede ser vista por el personal interno (técnicos, supervisores, administradores).
 *   • CUSTOMER → La nota también puede ser visible para el cliente asociado a la visita.
 *
 * Mantenibilidad:
 *   - Si se agregan nuevos tipos de visibilidad (por ejemplo, PARTNER o AUDITOR),
 *     deben reflejarse en los controladores y vistas que consumen este modelo.
 * -----------------------------------------------------------------------------
 */
package com.visits.model;

/**
 * Enumeración que define los niveles de visibilidad de una nota dentro del sistema de visitas.
 */
public enum NoteVisibility {
    // Nota visible únicamente para el personal interno.
    INTERNAL,
    // Nota visible también para el cliente asociado a la visita.
    CUSTOMER,
}