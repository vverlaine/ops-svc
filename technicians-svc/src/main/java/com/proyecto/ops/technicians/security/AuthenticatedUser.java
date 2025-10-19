/*
 * -----------------------------------------------------------------------------
 * AuthenticatedUser.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Representa al usuario autenticado dentro del microservicio "technicians-svc".
 *   Este record se utiliza para mantener los datos esenciales del usuario que
 *   ha iniciado sesión y que realiza peticiones autenticadas al sistema.
 *
 * Contexto de uso:
 *   - Se utiliza junto con el filtro AuthFilter y el CurrentUserArgumentResolver.
 *   - Permite acceder fácilmente a los datos del usuario autenticado dentro de
 *     los controladores y servicios del microservicio.
 *
 * Diseño:
 *   - Implementado como un `record` para garantizar inmutabilidad y simplicidad.
 *   - Contiene los campos principales del usuario autenticado:
 *       • id    → Identificador único del usuario.
 *       • name  → Nombre completo del usuario.
 *       • email → Correo electrónico del usuario.
 *       • role  → Rol o perfil del usuario (por ejemplo, ADMIN, TECHNICIAN).
 *
 * Mantenibilidad:
 *   - Si en el futuro se agregan nuevos atributos de seguridad (como permisos o zonas),
 *     deben añadirse aquí y en el filtro de autenticación que los establece.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.technicians.security;

import java.util.UUID;

/**
 * Record que representa al usuario autenticado.
 *
 * Contiene información básica del usuario, como su identificador, nombre,
 * correo electrónico y rol asignado dentro del sistema.
 */
public record AuthenticatedUser(
    // Identificador único del usuario autenticado (UUID).
    UUID id,
    // Nombre completo del usuario autenticado.
    String name,
    // Correo electrónico asociado al usuario autenticado.
    String email,
    // Rol o perfil del usuario dentro del sistema (ejemplo: ADMIN, TECHNICIAN, USER).
    String role
) {}