/*
 * -----------------------------------------------------------------------------
 * AuthFilter.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Filtro de autenticación encargado de validar el token Bearer en cada petición
 *   HTTP entrante del microservicio "supervisors-svc".
 *
 * Contexto de uso:
 *   - Se ejecuta una vez por cada solicitud (extiende OncePerRequestFilter).
 *   - Valida el token de autorización consultando el endpoint `/auth/me` del
 *     microservicio de autenticación (auth-svc).
 *   - Si el token es válido, inyecta un objeto AuthenticatedUser en el request.
 *   - Si el token es inválido o no existe, responde con HTTP 401 Unauthorized.
 *
 * Diseño:
 *   - Usa RestClient de Spring para comunicarse con el servicio de autenticación.
 *   - Excluye rutas públicas como `/actuator/health`, `/actuator/info` y `/error`.
 *
 * Mantenibilidad:
 *   - Puede ampliarse para incluir caché de tokens o manejo de roles.
 *   - En entornos productivos, conviene agregar logging detallado o trazas distribuidas.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.supervisors.security;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro que intercepta todas las solicitudes HTTP y valida su autenticación
 * mediante el microservicio "auth-svc".
 */
@Component
public class AuthFilter extends OncePerRequestFilter {

    // Cliente HTTP utilizado para validar tokens mediante el endpoint /auth/me.
  private final RestClient authRestClient;

    // Endpoints públicos que no requieren autenticación.
  private static final Set<String> PUBLIC = Set.of("/actuator/health", "/actuator/info", "/error");

    /**
     * Constructor que inyecta el cliente HTTP utilizado para validar tokens.
     *
     * @param authRestClient Cliente RestClient configurado para comunicarse con el servicio de autenticación.
     */
  public AuthFilter(@Qualifier("authRestClient") RestClient authRestClient) {
    this.authRestClient = authRestClient;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain chain) throws ServletException, IOException {
        // Obtiene la ruta solicitada.
    String path = request.getRequestURI();

        // Si la ruta solicitada pertenece a las rutas públicas, no requiere autenticación.
    if (isPublic(path, request.getMethod())) { chain.doFilter(request, response); return; }

        // Obtiene el encabezado Authorization de la solicitud.
    String h = request.getHeader("Authorization");

        // Si no existe encabezado o el formato no es Bearer, retorna 401 Unauthorized.
    if (h == null || !h.startsWith("Bearer ")) { unauthorized(response); return; }

        // Extrae el token eliminando el prefijo "Bearer ".
    String token = h.substring("Bearer ".length()).trim();

        // Llama al endpoint /auth/me del servicio de autenticación para validar el token.
    try {
      @SuppressWarnings("unchecked")
      Map<String, Object> me = authRestClient.get()
          .uri("/auth/me")
          .header("Authorization", "Bearer " + token)
          .retrieve()
          .body(Map.class);

        // Construye un objeto AuthenticatedUser con los datos devueltos por el servicio de autenticación.
      AuthenticatedUser user = new AuthenticatedUser(
          UUID.fromString((String) me.get("id")),
          (String) me.get("name"),
          (String) me.get("email"),
          (String) me.get("role")
      );

        // Agrega el usuario autenticado como atributo del request para su posterior uso.
      request.setAttribute(CurrentUserArgumentResolver.REQ_ATTR, user);
      chain.doFilter(request, response);
        // Si la validación falla (token inválido o expirado), responde con 401 Unauthorized.
    } catch (RestClientResponseException ex) {
      unauthorized(response);
    }
  }

    /**
     * Envía una respuesta HTTP 401 Unauthorized en formato JSON cuando el token es inválido o no existe.
     *
     * @param res Objeto HttpServletResponse donde se escribe la respuesta.
     * @throws IOException Si ocurre un error al escribir la respuesta.
     */
  private void unauthorized(HttpServletResponse res) throws IOException {
        // Establece el código de estado HTTP 401 (no autorizado).
    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // Escribe un cuerpo JSON simple con el mensaje de error.
    res.getWriter().write("{\"error\":\"Unauthorized\"}");
  }

  private boolean isPublic(String path, String method) {
    if (PUBLIC.contains(path)) {
      return true;
    }
    return "GET".equalsIgnoreCase(method) && path.startsWith("/supervisors");
  }
}
