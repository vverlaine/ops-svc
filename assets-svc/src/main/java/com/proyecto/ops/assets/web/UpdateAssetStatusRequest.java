/*
 * -----------------------------------------------------------------------------
 * UpdateAssetStatusRequest.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   DTO (Data Transfer Object) utilizado para actualizar el estado operativo
 *   de un activo (Asset) mediante el endpoint PATCH o PUT /assets/{id}/status.
 *
 * Contexto de uso:
 *   - Es recibido por el controlador AssetController cuando se desea cambiar
 *     el estado de un activo existente (por ejemplo, de IN_SERVICE a MAINTENANCE).
 *   - Permite encapsular únicamente el nuevo estado del activo sin necesidad
 *     de enviar todos los demás campos.
 *
 * Diseño:
 *   - Implementado como un `record` para garantizar inmutabilidad.
 *   - Incluye validación con @NotNull para asegurar que el campo `status`
 *     siempre sea proporcionado.
 *
 * Campo:
 *   status → Representa el nuevo estado del activo, definido por el enum AssetStatus.
 *
 * Ejemplo de uso:
 *   {
 *     "status": "OUT_OF_SERVICE"
 *   }
 *
 * Mantenibilidad:
 *   - Si en el futuro se requiere actualizar más atributos de estado (por ejemplo,
 *     fecha de cambio o motivo), pueden agregarse nuevos campos a este DTO.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.assets.web;

import com.proyecto.ops.assets.model.AssetStatus;

import jakarta.validation.constraints.NotNull;

/**
 * DTO para actualizar el estado de un activo.
 *
 * Utilizado por el controlador {@link com.proyecto.ops.assets.web.AssetController}
 * para recibir el nuevo estado operativo de un activo.
 */
public record UpdateAssetStatusRequest(
    // Estado nuevo del activo (obligatorio), definido en el enum AssetStatus.
    @NotNull AssetStatus status) {}