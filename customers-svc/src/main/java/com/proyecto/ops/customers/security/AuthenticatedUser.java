/*
 * -----------------------------------------------------------------------------
 * AuthenticatedUser.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Representa el modelo del usuario autenticado dentro del contexto de seguridad
 *   del microservicio "customers-svc".
 *
 * Contexto de uso:
 *   - Este objeto se utiliza para almacenar la información del usuario que ha
 *     sido validado por el servicio de autenticación (auth-svc).
 *   - Se propaga en el contexto de las peticiones HTTP autenticadas para permitir
 *     acceso controlado a recursos protegidos.
 *
 * Diseño:
 *   - Implementado como un `record` de Java para inmutabilidad y facilidad de uso.
 *   - Contiene los campos mínimos necesarios para representar la identidad del usuario:
 *       • id    → Identificador único del usuario.
 *       • name  → Nombre completo del usuario.
 *       • email → Correo electrónico registrado.
 *       • role  → Rol asignado (por ejemplo, ADMIN, USER, SUPERVISOR).
 *
 * Mantenibilidad:
 *   - Si se requiere agregar más información contextual (como permisos o país),
 *     pueden añadirse nuevos campos al record sin afectar su propósito principal.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.customers.security;

import java.util.UUID;

/**
 * Record que representa la información básica del usuario autenticado.
 *
 * Utilizado dentro del contexto de seguridad para identificar al usuario
 * actual en peticiones protegidas.
 */
public record AuthenticatedUser(
    // Identificador único del usuario autenticado.
    UUID id,
    // Nombre completo del usuario.
    String name,
    // Correo electrónico asociado al usuario.
    String email,
    // Rol o perfil asignado al usuario (ejemplo: ADMIN, USER, SUPERVISOR).
    String role
) {}