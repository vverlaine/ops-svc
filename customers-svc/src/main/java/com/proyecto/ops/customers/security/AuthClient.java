/*
 * -----------------------------------------------------------------------------
 * AuthClient.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Cliente HTTP encargado de comunicarse con el microservicio de autenticación
 *   para validar y obtener información del usuario autenticado.
 *
 * Contexto de uso:
 *   - Utilizado dentro del microservicio "customers-svc" para verificar tokens
 *     y obtener el usuario actual mediante el endpoint `/auth/me`.
 *   - Permite integrar seguridad basada en tokens (Bearer JWT) en peticiones
 *     que requieren autenticación.
 *
 * Diseño:
 *   - Anotado con @Component para ser gestionado por el contenedor de Spring.
 *   - Inyecta el RestClient configurado con el alias "authRestClient", definido
 *     en la clase ClientsConfig.
 *   - Expone el método `me(String bearerToken)` que consulta la identidad del usuario.
 *
 * Mantenibilidad:
 *   - Si se agregan más endpoints relacionados con autenticación (por ejemplo,
 *     refresh o logout), pueden implementarse en esta misma clase.
 *   - Es recomendable agregar manejo de excepciones en entornos productivos
 *     para gestionar errores de red o tokens inválidos.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.customers.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Cliente HTTP para interactuar con el servicio de autenticación (auth-svc).
 *
 * Permite obtener información del usuario autenticado a partir de un token JWT.
 */
@Component
public class AuthClient {

  private final RestClient client;

    /**
     * Constructor que recibe el cliente HTTP preconfigurado.
     *
     * @param client Cliente REST inyectado con la configuración base del servicio de autenticación.
     */
  public AuthClient(@Qualifier("authRestClient") RestClient client) {
    this.client = client;
  }

    /**
     * Obtiene la información del usuario autenticado desde el endpoint `/auth/me`.
     *
     * @param bearerToken Token JWT utilizado para autenticar la solicitud.
     * @return Cadena JSON con los datos del usuario autenticado.
     */
  public String me(String bearerToken) {
        // Realiza una llamada GET al endpoint /auth/me agregando el token en el encabezado Authorization.
        return client.get()
            .uri("/auth/me")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
            .retrieve()
            .body(String.class);
  }
}