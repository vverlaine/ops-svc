/*
 * -----------------------------------------------------------------------------
 * TicketsClient.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Cliente HTTP que permite la comunicación entre el microservicio "work-orders-svc"
 *   y el microservicio "tickets-svc".
 *
 * Contexto de uso:
 *   - Se utiliza para verificar la existencia de tickets antes de asociarlos a una
 *     orden de trabajo (work order).
 *   - Envía solicitudes REST al endpoint remoto definido en la propiedad
 *     `tickets.base-url`.
 *
 * Diseño:
 *   - Anotado con @Component para que Spring lo gestione como bean inyectable.
 *   - Usa `RestClient` (API moderna de Spring 6) para realizar peticiones HTTP.
 *   - Implementa logs con SLF4J para registrar errores en la comunicación.
 *
 * Flujo principal:
 *   • exists(UUID id) → Envía una solicitud GET al servicio de tickets.
 *     - Si el servicio responde con un código 2xx, devuelve `true`.
 *     - Si ocurre una excepción o el servicio no responde, lanza RestClientException.
 *
 * Mantenibilidad:
 *   - Si cambia el endpoint o la autenticación del servicio "tickets-svc",
 *     se deben ajustar las propiedades o el cliente HTTP.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.workorders.clients;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Cliente HTTP para interactuar con el microservicio "tickets-svc".
 *
 * Permite verificar la existencia de tickets por su identificador UUID.
 */
@Component
public class TicketsClient {
    // Logger para registrar eventos e información de diagnóstico.
  private static final Logger log = LoggerFactory.getLogger(TicketsClient.class);
    // Cliente HTTP utilizado para realizar las solicitudes REST al servicio de tickets.
  private final RestClient http;

    /**
     * Constructor que inicializa el cliente HTTP con la URL base del servicio de tickets.
     *
     * @param builder  Constructor del RestClient proporcionado por Spring.
     * @param baseUrl  URL base del microservicio "tickets-svc" (configurable por propiedades).
     */
  public TicketsClient(RestClient.Builder builder,
                       @Value("${tickets.base-url:http://localhost:8080}") String baseUrl) {
      // Construye el cliente REST con la URL base especificada.
    this.http = builder.baseUrl(baseUrl).build();
  }

    /**
     * Verifica si un ticket existe en el microservicio "tickets-svc".
     *
     * @param id Identificador único del ticket.
     * @return true si el ticket existe (respuesta HTTP 2xx), false en caso contrario.
     * @throws RestClientException si el servicio no está disponible o ocurre un error de conexión.
     */
  public boolean exists(UUID id) {
      // Envía una solicitud GET al endpoint /tickets/{id} y obtiene el código de respuesta.
    try {
      HttpStatusCode code = http.get().uri("/tickets/{id}", id)
          .exchange((req, res) -> res.getStatusCode());
          // Devuelve true si el código de estado indica éxito (2xx).
      return code.is2xxSuccessful();
        // Si ocurre un error al comunicarse con el servicio, se registra y se relanza la excepción.
    } catch (RestClientException e) {
      log.error("tickets-svc unreachable when fetching id={}: {}", id, e.getMessage());
      throw e; // lo manejamos en el controller como 503
    }
  }
}