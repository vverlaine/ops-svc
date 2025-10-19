/*
 * -----------------------------------------------------------------------------
 * CurrentUser.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Define una anotación personalizada que permite inyectar automáticamente
 *   el usuario autenticado (AuthenticatedUser) en los métodos de los controladores.
 *
 * Contexto de uso:
 *   - Se utiliza junto con el CurrentUserArgumentResolver.
 *   - Permite acceder al usuario actual sin necesidad de obtenerlo manualmente
 *     desde el token o el contexto de seguridad.
 *
 * Diseño:
 *   - Meta-anotaciones:
 *       @Target(ElementType.PARAMETER) → Solo puede aplicarse a parámetros de métodos.
 *       @Retention(RetentionPolicy.RUNTIME) → Disponible en tiempo de ejecución.
 *       @Documented → Incluye la anotación en la documentación generada por Javadoc.
 *
 * Ejemplo de uso:
 *   @GetMapping("/me")
 *   public ResponseEntity<?> getProfile(@CurrentUser AuthenticatedUser user) {
 *       return ResponseEntity.ok(user);
 *   }
 *
 * Mantenibilidad:
 *   - Si se amplía el modelo de seguridad, esta anotación puede seguir siendo
 *     utilizada sin cambios.
 *   - Facilita la legibilidad y reduce el acoplamiento entre capas de autenticación y presentación.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.customers.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
/**
 * Anotación personalizada para inyectar automáticamente el usuario autenticado
 * en los controladores.
 *
 * Debe ser procesada por un {@link com.proyecto.ops.customers.security.CurrentUserArgumentResolver}.
 */
public @interface CurrentUser {}