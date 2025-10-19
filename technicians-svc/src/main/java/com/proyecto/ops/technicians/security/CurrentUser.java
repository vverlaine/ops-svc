/*
 * -----------------------------------------------------------------------------
 * CurrentUser.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Define una anotación personalizada que permite inyectar automáticamente
 *   el usuario autenticado (AuthenticatedUser) en los métodos de los controladores.
 *
 * Contexto de uso:
 *   - Pertenece al microservicio "technicians-svc".
 *   - Se utiliza junto con el CurrentUserArgumentResolver, el cual procesa esta
 *     anotación para insertar el usuario autenticado en los controladores.
 *
 * Diseño:
 *   - Meta-anotaciones:
 *       @Target(ElementType.PARAMETER) → Solo puede aplicarse a parámetros de método.
 *       @Retention(RetentionPolicy.RUNTIME) → Se conserva en tiempo de ejecución.
 *
 * Ejemplo de uso:
 *   @GetMapping("/me")
 *   public ResponseEntity<?> getProfile(@CurrentUser AuthenticatedUser user) {
 *       return ResponseEntity.ok(user);
 *   }
 *
 * Mantenibilidad:
 *   - Si se amplía la estructura del usuario autenticado, esta anotación seguirá
 *     siendo compatible, ya que depende únicamente del resolvedor configurado.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.technicians.security;

import static java.lang.annotation.ElementType.PARAMETER;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

@Target(PARAMETER)
@Retention(RUNTIME)
/**
 * Anotación personalizada utilizada para inyectar el usuario autenticado
 * en los métodos de los controladores.
 *
 * Debe ser procesada por un {@link com.proyecto.ops.technicians.security.CurrentUserArgumentResolver}.
 */
public @interface CurrentUser {}