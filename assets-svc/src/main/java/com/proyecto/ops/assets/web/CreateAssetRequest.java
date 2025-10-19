/*
 * -----------------------------------------------------------------------------
 * CreateAssetRequest.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Representa el objeto de solicitud (Request DTO) utilizado al crear un nuevo
 *   activo (Asset) mediante el endpoint POST /assets.
 *
 * Contexto de uso:
 *   - Recibido por el método create() del AssetController.
 *   - Contiene los datos necesarios para registrar un nuevo activo en la base de datos.
 *
 * Diseño:
 *   - Implementado como un `record` para garantizar inmutabilidad y claridad.
 *   - Incluye validaciones con anotaciones Jakarta Validation (@NotNull, @NotBlank)
 *     para asegurar la integridad de los datos de entrada.
 *
 * Campos:
 *   customerId   → UUID del cliente propietario (obligatorio).
 *   siteId       → UUID del sitio donde está ubicado el activo (opcional).
 *   serialNumber → Número de serie del activo (obligatorio).
 *   model        → Modelo o descripción técnica del activo.
 *   type         → Tipo o categoría del activo (por ejemplo, "Refrigerador").
 *   installedAt  → Fecha de instalación del activo.
 *   notes        → Comentarios o información adicional sobre el activo.
 *
 * Mantenibilidad:
 *   - Si se agregan nuevos campos al crear un activo, deben incluirse aquí
 *     con sus respectivas validaciones.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.assets.web;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de solicitud para crear un nuevo activo (Asset).
 *
 * Utilizado por el controlador {@link com.proyecto.ops.assets.web.AssetController}
 * en el endpoint POST `/assets`.
 */
public record CreateAssetRequest(
        // UUID del cliente al que pertenece el activo (obligatorio).
        @NotNull UUID customerId,
        // UUID del sitio donde está ubicado el activo (opcional).
        UUID siteId,
        // Número de serie único del activo (obligatorio, no puede estar vacío).
        @NotBlank String serialNumber,
        // Modelo o descripción técnica del activo.
        String model,
        // Tipo o categoría del activo (por ejemplo, "Refrigerador", "Display").
        String type,
        // Fecha de instalación del activo.
        LocalDate installedAt,
        // Comentarios o notas adicionales sobre el activo.
        String notes
) {}