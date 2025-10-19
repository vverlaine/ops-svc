/*
 * -----------------------------------------------------------------------------
 * UpdateContactRequest.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   DTO (Data Transfer Object) utilizado para recibir datos en la actualización
 *   de un contacto existente mediante el endpoint PUT /contacts/{id}.
 *
 * Contexto de uso:
 *   - Es consumido por el método update() (o su equivalente) dentro del ContactController.
 *   - Permite modificar la información básica de un contacto registrado, como
 *     nombre, correo electrónico, teléfono o rol.
 *
 * Diseño:
 *   - Implementado como un `record` de Java, garantizando inmutabilidad y
 *     simplicidad en el transporte de datos.
 *   - Incluye validación de campo obligatorio con @NotBlank para el nombre.
 *
 * Campos:
 *   name  → Nombre completo del contacto (obligatorio).
 *   email → Correo electrónico (opcional).
 *   phone → Número telefónico (opcional).
 *   role  → Rol o cargo del contacto dentro del cliente.
 *
 * Mantenibilidad:
 *   - Si se amplía el modelo de contacto con nuevos campos actualizables,
 *     estos deben incluirse en este DTO.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.contacts.web;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para la actualización de un contacto existente.
 *
 * Se utiliza en el endpoint PUT `/contacts/{id}` del
 * {@link com.proyecto.ops.contacts.web.ContactController}.
 */
public record UpdateContactRequest(
        // Nombre completo del contacto (obligatorio, no puede estar vacío).
        @NotBlank String name,
        // Correo electrónico actualizado del contacto (opcional).
        String email,
        // Nuevo número telefónico del contacto (opcional).
        String phone,
        // Rol o función actualizada del contacto dentro de la organización.
        String role
) {}