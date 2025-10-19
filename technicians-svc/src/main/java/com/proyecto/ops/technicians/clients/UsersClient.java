/*
 * -----------------------------------------------------------------------------
 * UsersClient.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Cliente HTTP responsable de comunicarse con el microservicio "users-svc"
 *   para obtener información básica de usuarios, principalmente sus nombres.
 *
 * Contexto de uso:
 *   - Pertenece al microservicio "technicians-svc".
 *   - Se utiliza para consultar datos de usuario desde endpoints del servicio
 *     de usuarios (por ejemplo, para mostrar el nombre del técnico asignado).
 *
 * Diseño:
 *   - Anotado con @Component para que Spring gestione su ciclo de vida.
 *   - Utiliza RestClient de Spring Framework 6 para realizar peticiones HTTP.
 *   - La URL base se inyecta desde la propiedad de configuración `users.base-url`.
 *   - Implementa un manejo de errores simple con logs y retorno por defecto.
 *
 * Métodos principales:
 *   getNameOrUnknown(UUID id) → Devuelve el nombre del usuario o "Unknown" si no puede obtenerlo.
 *
 * Mantenibilidad:
 *   - Si el servicio "users-svc" amplía sus endpoints, se pueden agregar
 *     nuevos métodos aquí para consultarlos.
 *   - En un entorno productivo, podría beneficiarse de mecanismos de resiliencia
 *     como circuit breakers o caché local.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.technicians.clients;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Cliente HTTP para interactuar con el servicio externo "users-svc".
 *
 * Permite obtener información básica de los usuarios (como su nombre).
 */
@Component
public class UsersClient {
  private static final Logger log = LoggerFactory.getLogger(UsersClient.class);
  private final RestClient http;

    /**
     * Constructor que inicializa el cliente HTTP con una URL base configurable.
     *
     * @param builder  Builder proporcionado por Spring para construir el RestClient.
     * @param baseUrl  URL base del servicio de usuarios (por defecto: http://localhost:8081).
     */
  public UsersClient(RestClient.Builder builder,
                     @Value("${users.base-url:http://localhost:8081}") String baseUrl) {
        // Construye el cliente HTTP con la URL base definida en la configuración.
        this.http = builder.baseUrl(baseUrl).build();
  }

    /**
     * Obtiene el nombre del usuario desde el servicio "users-svc" usando su UUID.
     *
     * @param id Identificador único del usuario.
     * @return El nombre del usuario o "Unknown" si no se puede obtener o está vacío.
     */
  public String getNameOrUnknown(UUID id) {
        // Si el ID es nulo, no se realiza la consulta.
        if (id == null) return null;
        try {
            // Realiza la solicitud GET al endpoint /users/{id} y mapea la respuesta al DTO interno.
            UserDto dto = http.get().uri("/users/{id}", id).retrieve().body(UserDto.class);
            // Si el usuario tiene nombre válido, se devuelve; de lo contrario, retorna "Unknown".
            return (dto != null && dto.name != null && !dto.name.isBlank()) ? dto.name : "Unknown";
        // Captura errores de conexión o respuesta y registra el incidente en los logs.
        } catch (RestClientException e) {
            log.error("users-svc unreachable id={}: {}", id, e.getMessage());
            return "Unknown";
        }
  }

    /**
     * Clase interna utilizada para mapear la respuesta JSON del servicio "users-svc".
     *
     * Contiene únicamente los campos básicos: id y name.
     */
  public static class UserDto { public UUID id; public String name; }
}