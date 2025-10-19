/*
 * -----------------------------------------------------------------------------
 * CustomersClient.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Cliente HTTP encargado de comunicarse con el microservicio "customers-svc"
 *   para validar la existencia de clientes y obtener sus nombres.
 *
 * Contexto de uso:
 *   - Pertenece al microservicio "tickets-svc".
 *   - Se utiliza para verificar si un cliente existe antes de asociarlo a un ticket
 *     y para mostrar su nombre en respuestas o reportes.
 *
 * Diseño:
 *   - Anotado con @Component para ser administrado por el contenedor de Spring.
 *   - Utiliza RestClient (Spring Framework 6) para realizar peticiones HTTP.
 *   - La URL base se inyecta desde la propiedad `customers.base-url`, con valor
 *     predeterminado `http://localhost:8081`.
 *   - Implementa manejo de errores con logs y tolerancia ante caídas del servicio
 *     de clientes, evitando bloquear la creación de tickets.
 *
 * Métodos principales:
 *   - exists(UUID id)
 *       → Verifica si un cliente existe en el servicio remoto.
 *   - getNameOrUnknown(UUID id)
 *       → Obtiene el nombre del cliente o devuelve "Unknown" si no se encuentra.
 *
 * Mantenibilidad:
 *   - Si el microservicio de clientes amplía su API, se pueden agregar aquí
 *     nuevos métodos para consumir esos endpoints.
 * -----------------------------------------------------------------------------
 */
// tickets-svc/src/main/java/com/proyecto/ops/tickets/clients/CustomersClient.java
package com.proyecto.ops.tickets.clients;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Cliente HTTP para interactuar con el microservicio "customers-svc".
 *
 * Permite validar clientes y obtener sus nombres.
 */
@Component
public class CustomersClient {
    private static final Logger log = LoggerFactory.getLogger(CustomersClient.class);
    private final RestClient http;

    /**
     * Constructor que inicializa el cliente HTTP con la URL base configurada.
     *
     * @param builder Builder proporcionado por Spring para construir el RestClient.
     * @param baseUrl URL base del servicio de clientes (por defecto: http://localhost:8081).
     */
    public CustomersClient(RestClient.Builder builder,
                           @Value("${customers.base-url:http://localhost:8081}") String baseUrl) {
        // Construye el cliente HTTP usando la URL base definida en la configuración.
        this.http = builder.baseUrl(baseUrl).build();
    }

    /**
     * Verifica si un cliente existe en el microservicio "customers-svc".
     *
     * @param id Identificador único del cliente.
     * @return true si el cliente existe, false si no se encuentra.
     *         Si el servicio está caído, retorna true para no bloquear operaciones.
     */
    public boolean exists(UUID id) {
        try {
            // Realiza una petición GET al endpoint /customers/{id} para verificar existencia.
            http.get()
                .uri("/customers/{id}", id)
                .retrieve()
                .toBodilessEntity();
            return true;
            // Si el servicio responde con 404 Not Found, el cliente no existe.
        } catch (HttpClientErrorException.NotFound e) {
            return false;
            // Si hay un error de conexión o el servicio no está disponible,
            // se registra una advertencia pero se asume que el cliente existe
            // para no interrumpir la creación del ticket.
        } catch (RestClientException e) {
            log.warn("customers-svc unreachable when checking id={}: {}", id, e.getMessage());
            return true;
        }
    }

    /**
     * Obtiene el nombre del cliente desde el microservicio "customers-svc".
     *
     * @param id Identificador único del cliente.
     * @return Nombre del cliente, o "Unknown" si no se puede obtener.
     */
    public String getNameOrUnknown(UUID id) {
        try {
            // Llama al endpoint /customers/{id} y mapea la respuesta al DTO interno CustomerDto.
            var dto = http.get()
                    .uri("/customers/{id}", id)
                    .retrieve()
                    .body(CustomerDto.class);
            // Devuelve el nombre del cliente si está disponible; de lo contrario, "Unknown".
            return (dto != null && dto.name != null && !dto.name.isBlank()) ? dto.name : "Unknown";
            // Si el servicio no responde o ocurre un error, se registra en logs y retorna "Unknown".
        } catch (RestClientException e) {
            log.error("customers-svc unreachable when fetching id={}: {}", id, e.getMessage());
            return "Unknown";
        }
    }

    /**
     * Clase interna utilizada para mapear la respuesta JSON del servicio "customers-svc".
     */
    public static class CustomerDto {
        // Identificador único del cliente.
        public UUID id;
        // Nombre del cliente.
        public String name;
    }
}