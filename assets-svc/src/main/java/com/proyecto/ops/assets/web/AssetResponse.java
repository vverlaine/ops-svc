/*
 * -----------------------------------------------------------------------------
 * AssetResponse.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Representa el DTO (Data Transfer Object) utilizado para enviar información
 *   de un activo (Asset) en las respuestas HTTP del controlador.
 *
 * Contexto de uso:
 *   - Es retornado por el controlador AssetController.
 *   - Se utiliza para serializar los datos de un activo en formato JSON hacia el cliente.
 *
 * Diseño:
 *   - Implementado como un `record` de Java, lo que proporciona:
 *       • Inmutabilidad automática (campos finales).
 *       • Implementaciones implícitas de equals(), hashCode() y toString().
 *   - Contiene los mismos campos que la entidad Asset, pero sin exponer lógica interna.
 *
 * Campos:
 *   id           → Identificador único del activo.
 *   customerId   → UUID del cliente propietario.
 *   siteId       → UUID del sitio asociado (opcional).
 *   serialNumber → Número de serie del activo.
 *   model        → Modelo o descripción técnica del activo.
 *   type         → Tipo o categoría del activo.
 *   installedAt  → Fecha de instalación.
 *   notes        → Notas o comentarios adicionales.
 *   createdAt    → Fecha de creación del registro.
 *
 * Mantenibilidad:
 *   - Si se agregan nuevos campos a la entidad Asset, deben incluirse aquí
 *     para mantener consistencia en las respuestas del API.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.assets.web;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO (Data Transfer Object) que representa un activo en las respuestas del API.
 *
 * Se utiliza en el controlador {@link com.proyecto.ops.assets.web.AssetController}
 * para estructurar la salida enviada al cliente.
 */
public record AssetResponse(
        UUID id,
        UUID customerId,
        UUID siteId,
        String serialNumber,
        String model,
        String type,
        LocalDate installedAt,
        String notes,
        Instant createdAt
) {}