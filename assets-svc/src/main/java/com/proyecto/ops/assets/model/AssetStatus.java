/*
 * -----------------------------------------------------------------------------
 * AssetStatus.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Enumeración que define los posibles estados operativos de un activo (Asset)
 *   dentro del sistema.
 *
 * Contexto de uso:
 *   - Asociado a la entidad Asset.java para indicar su estado actual.
 *   - Permite controlar la disponibilidad o condición de funcionamiento de cada
 *     activo físico gestionado.
 *
 * Estados definidos:
 *   IN_SERVICE      → El activo está en operación normal y disponible.
 *   MAINTENANCE     → El activo se encuentra en mantenimiento preventivo o correctivo.
 *   OUT_OF_SERVICE  → El activo está fuera de servicio, averiado o dado de baja.
 *
 * Mantenibilidad:
 *   - Se puede extender fácilmente si en el futuro se agregan nuevos estados.
 *   - Ideal para usar en validaciones, filtros o reglas de negocio.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.assets.model;

/**
 * Enum que representa los posibles estados de un activo físico.
 *
 * Se utiliza dentro de la entidad {@link com.proyecto.ops.assets.model.Asset}
 * para reflejar su condición actual en el sistema.
 */
public enum AssetStatus {
    // Activo en operación normal, funcional y disponible.
    IN_SERVICE,
    // Activo en proceso de mantenimiento preventivo o correctivo.
    MAINTENANCE,
    // Activo fuera de servicio, averiado o dado de baja.
    OUT_OF_SERVICE
}