/*
 * -----------------------------------------------------------------------------
 * CreateSiteRequest.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   DTO (Data Transfer Object) utilizado para recibir los datos de entrada
 *   al crear un nuevo sitio (CustomerSite) mediante el endpoint POST /sites.
 *
 * Contexto de uso:
 *   - Este record se emplea dentro del microservicio "sites-svc".
 *   - Es consumido por el controlador responsable de la creación de sitios,
 *     generalmente en el método `create()`.
 *
 * Diseño:
 *   - Implementado como un `record` de Java, lo que garantiza inmutabilidad
 *     y simplicidad en la transferencia de datos.
 *   - Utiliza anotaciones de validación Jakarta:
 *       • @NotNull → El campo no puede ser nulo.
 *       • @NotBlank → El campo no puede ser vacío o solo contener espacios.
 *
 * Campos:
 *   customerId → UUID del cliente propietario del sitio (obligatorio).
 *   name       → Nombre o descripción del sitio (obligatorio).
 *   address    → Dirección física del sitio (opcional).
 *   city       → Ciudad del sitio (opcional).
 *   state      → Estado o provincia (opcional).
 *   country    → País (opcional).
 *
 * Mantenibilidad:
 *   - Si se agregan nuevos campos al modelo CustomerSite, deben reflejarse aquí.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.sites.web;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de solicitud para crear un nuevo sitio asociado a un cliente.
 *
 * Utilizado por el endpoint POST `/sites` del controlador de sitios.
 */
public record CreateSiteRequest(
        // Identificador UUID del cliente al que pertenece el sitio (obligatorio).
        @NotNull UUID customerId,

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