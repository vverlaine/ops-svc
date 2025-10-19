/*
 * -----------------------------------------------------------------------------
 * UpdateAssetRequest.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   DTO (Data Transfer Object) que representa la solicitud para actualizar
 *   los datos de un activo existente mediante el endpoint PUT /assets/{id}.
 *
 * Contexto de uso:
 *   - Utilizado por el método update() del AssetController.
 *   - Permite modificar parcialmente la información de un activo ya registrado.
 *
 * Diseño:
 *   - Implementado como un `record` de Java, lo que garantiza inmutabilidad
 *     y una representación clara de los datos transferidos.
 *   - Incluye validaciones con anotaciones Jakarta Validation para campos
 *     que deben cumplir requisitos específicos (como el número de serie).
 *
 * Campos:
 *   type         → Tipo o categoría del activo (por ejemplo, "Display", "Cooler").
 *   model        → Modelo o descripción técnica del activo.
 *   serialNumber → Número de serie único del activo (obligatorio, no puede estar vacío).
 *   siteId       → UUID del sitio donde está ubicado el activo.
 *   installedAt  → Fecha en la que fue instalado el activo.
 *   notes        → Comentarios o información adicional.
 *
 * Mantenibilidad:
 *   - Si se amplía el modelo de activos, deben agregarse los nuevos campos
 *     aquí y en el método update() del controlador.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.assets.web;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para la actualización de activos existentes.
 *
 * Se utiliza en el endpoint PUT `/assets/{id}` del
 * {@link com.proyecto.ops.assets.web.AssetController}.
 */
public record UpdateAssetRequest(
        // Tipo o categoría del activo (por ejemplo, "Display", "Refrigerador").
        String type,
        // Modelo o referencia técnica del activo.
        String model,
        // Número de serie del activo (obligatorio, no puede ser vacío).
        @NotBlank String serialNumber,
        // UUID del sitio donde está instalado el activo.
        UUID siteId,
        // Fecha de instalación o puesta en funcionamiento del activo.
        LocalDate installedAt,
        // Notas o comentarios adicionales sobre el activo.
        String notes
) {}