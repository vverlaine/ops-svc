/*
 * -----------------------------------------------------------------------------
 * CreateContactRequest.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Representa el DTO (Data Transfer Object) utilizado para recibir datos
 *   de entrada al crear un nuevo contacto mediante el endpoint POST /contacts.
 *
 * Contexto de uso:
 *   - Es consumido por el método create() del ContactController.
 *   - Permite validar los campos requeridos para registrar un nuevo contacto
 *     asociado a un cliente.
 *
 * Diseño:
 *   - Implementado como un `record` de Java para mayor concisión e inmutabilidad.
 *   - Utiliza anotaciones de validación Jakarta (@NotNull, @NotBlank) para
 *     asegurar que los campos obligatorios sean proporcionados.
 *
 * Campos:
 *   customerId → UUID del cliente propietario del contacto (obligatorio).
 *   name       → Nombre completo del contacto (obligatorio).
 *   email      → Correo electrónico del contacto (opcional).
 *   phone      → Número telefónico (opcional).
 *   role       → Cargo o función del contacto dentro del cliente.
 *
 * Mantenibilidad:
 *   - Si se agregan nuevos campos requeridos por la creación de contactos,
 *     deben definirse aquí con sus respectivas validaciones.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.contacts.web;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de solicitud para crear un nuevo contacto asociado a un cliente.
 *
 * Utilizado por el endpoint POST `/contacts` del
 * {@link com.proyecto.ops.contacts.web.ContactController}.
 */
public record CreateContactRequest(
    // UUID del cliente al que pertenece el contacto (obligatorio).
    @NotNull UUID customerId,
    // Nombre completo del contacto (obligatorio, no puede estar vacío).
    @NotBlank String name,
    // Correo electrónico del contacto (opcional).
    String email,
    // Número telefónico del contacto (opcional).
    String phone,
    // Rol o cargo del contacto dentro de la organización del cliente.
    String role
) {}