/*
 * -----------------------------------------------------------------------------
 * UpdateSiteRequest.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   DTO (Data Transfer Object) utilizado para recibir los datos de actualización
 *   de un sitio existente mediante el endpoint PUT /sites/{id}.
 *
 * Contexto de uso:
 *   - Forma parte del microservicio "sites-svc".
 *   - Permite modificar los campos básicos de un sitio ya registrado, como
 *     nombre, dirección, ciudad, estado o país.
 *
 * Diseño:
 *   - Implementado como un `record` de Java para inmutabilidad y claridad.
 *   - Utiliza la anotación @NotBlank para validar que el nombre no sea nulo ni vacío.
 *
 * Campos:
 *   name    → Nombre o descripción del sitio (obligatorio).
 *   address → Dirección física del sitio (opcional).
 *   city    → Ciudad del sitio (opcional).
 *   state   → Estado o provincia (opcional).
 *   country → País donde se encuentra el sitio (opcional).
 *
 * Mantenibilidad:
 *   - Si se amplía el modelo de CustomerSite, deben agregarse los nuevos campos aquí.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.sites.web;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO utilizado para actualizar los datos de un sitio existente.
 *
 * Utilizado por el endpoint PUT `/sites/{id}` del controlador
 * {@link com.proyecto.ops.sites.web.SiteController}.
 */
public record UpdateSiteRequest(
        // Nombre o descripción del sitio (obligatorio, no puede estar vacío).
        @NotBlank String name,
        // Dirección física del sitio (opcional).
        String address,
        // Ciudad donde se ubica el sitio (opcional).
        String city,
        // Estado o provincia del sitio (opcional).
        String state,
        // País donde se encuentra el sitio (opcional).
        String country
) {}