/*
 * -----------------------------------------------------------------------------
 * ContactResponse.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Representa el DTO (Data Transfer Object) utilizado para devolver la información
 *   de un contacto a los clientes del API REST del microservicio contacts-svc.
 *
 * Contexto de uso:
 *   - Es devuelto por el controlador ContactController en los endpoints GET y POST.
 *   - Se utiliza para serializar los datos de un contacto en formato JSON.
 *
 * Diseño:
 *   - Implementado como un `record` de Java para inmutabilidad y concisión.
 *   - Incluye tanto los datos básicos del contacto como el nombre del cliente
 *     obtenido desde el servicio externo "customers".
 *
 * Campos:
 *   id            → Identificador único del contacto.
 *   customerId    → UUID del cliente asociado al contacto.
 *   customerName  → Nombre del cliente (resuelto por CustomersClient).
 *   name          → Nombre del contacto.
 *   email         → Correo electrónico.
 *   phone         → Número telefónico.
 *   role          → Cargo o función del contacto dentro del cliente.
 *   createdAt     → Fecha de creación del registro.
 *
 * Mantenibilidad:
 *   - Si se agregan nuevos atributos a la entidad Contact, deben reflejarse aquí
 *     para mantener consistencia en las respuestas del API.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.contacts.web;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO (Data Transfer Object) que representa un contacto en las respuestas del API REST.
 *
 * Se utiliza en {@link com.proyecto.ops.contacts.web.ContactController} para
 * devolver los datos de los contactos con información del cliente asociada.
 */
public record ContactResponse(
    UUID id,
    UUID customerId,
    String customerName,
    String name,
    String email,
    String phone,
    String role,
    OffsetDateTime createdAt
) {}