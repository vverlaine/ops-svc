/*
 * -----------------------------------------------------------------------------
 * CustomerBasic.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Representa un objeto de transferencia de datos (DTO) básico que contiene
 *   la información esencial de un cliente. Se utiliza principalmente para
 *   respuestas resumidas o consultas livianas del microservicio "customers-svc".
 *
 * Contexto de uso:
 *   - Devuelto por controladores o servicios que no requieren la información
 *     completa del cliente (por ejemplo, listados o referencias en otros módulos).
 *   - Se utiliza como modelo de respuesta o proyección simplificada de la entidad Customer.
 *
 * Diseño:
 *   - Implementado como un `record` de Java, lo que proporciona:
 *       • Inmutabilidad.
 *       • Implementaciones automáticas de equals(), hashCode() y toString().
 *   - Contiene los campos más relevantes del cliente, sin incluir relaciones complejas.
 *
 * Campos:
 *   id         → Identificador único del cliente (UUID).
 *   name       → Nombre o razón social del cliente.
 *   taxId      → Identificador fiscal o número de registro tributario.
 *   email      → Correo electrónico del cliente.
 *   phone      → Teléfono de contacto.
 *   createdAt  → Fecha y hora en que se registró el cliente.
 *
 * Mantenibilidad:
 *   - Si se agregan nuevos campos relevantes al modelo de cliente, deben
 *     incorporarse aquí para mantener coherencia con las respuestas del API.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.customers.model;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO básico que representa la información esencial de un cliente.
 *
 * Utilizado en respuestas resumidas del microservicio "customers-svc".
 */
public record CustomerBasic(
    // Identificador único del cliente (UUID).
    UUID id,
    // Nombre o razón social del cliente.
    String name,
    // Identificador fiscal o número tributario del cliente.
    String taxId,
    // Correo electrónico principal del cliente.
    String email,
    // Número de teléfono de contacto.
    String phone,
    // Dirección principal del cliente.
    String address,
    // Fecha y hora de creación del registro del cliente.
    OffsetDateTime createdAt
) {}
