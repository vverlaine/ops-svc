/*
 * -----------------------------------------------------------------------------
 * SiteResponse.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   DTO (Data Transfer Object) que representa la respuesta enviada al cliente
 *   al consultar o crear un sitio (CustomerSite) en el microservicio "sites-svc".
 *
 * Contexto de uso:
 *   - Devuelto por el controlador SiteController en los endpoints GET, POST y PUT.
 *   - Permite serializar los datos del sitio a formato JSON para las respuestas HTTP.
 *
 * Diseño:
 *   - Implementado como un `record` de Java, lo que garantiza inmutabilidad y
 *     una representación concisa de datos.
 *   - Contiene los mismos campos principales que la entidad CustomerSite.
 *
 * Campos:
 *   id          → Identificador único del sitio.
 *   customerId  → UUID del cliente propietario.
 *   name        → Nombre o descripción del sitio.
 *   address     → Dirección física del sitio.
 *   city        → Ciudad donde se encuentra el sitio.
 *   state       → Estado o provincia.
 *   country     → País donde se ubica.
 *   createdAt   → Fecha y hora en que se creó el sitio.
 *
 * Mantenibilidad:
 *   - Si se agregan nuevos atributos a la entidad CustomerSite, deben reflejarse aquí.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.sites.web;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO de respuesta que representa un sitio (CustomerSite).
 *
 * Utilizado en las respuestas del controlador {@link com.proyecto.ops.sites.web.SiteController}.
 */
public record SiteResponse(
        // Identificador único del sitio (UUID).
        UUID id,
        // Identificador del cliente propietario del sitio.
        UUID customerId,
        // Nombre o descripción del sitio.
        String name,
        // Dirección física del sitio.
        String address,
        // Ciudad donde está ubicado el sitio.
        String city,
        // Estado o provincia del sitio.
        String state,
        // País donde se encuentra el sitio.
        String country,
        // Fecha y hora en que se creó el sitio (establecida por la base de datos).
        OffsetDateTime createdAt
) {}