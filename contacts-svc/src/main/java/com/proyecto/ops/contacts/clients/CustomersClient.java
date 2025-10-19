/*
 * -----------------------------------------------------------------------------
 * CustomersClient.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Cliente HTTP para comunicarse con el microservicio de "customers"
 *   y obtener información básica de clientes, como su nombre.
 *
 * Contexto de uso:
 *   - Este cliente se utiliza dentro del servicio contacts-svc.
 *   - Permite resolver nombres de clientes a partir de su UUID.
 *
 * Diseño:
 *   - Utiliza la clase RestClient de Spring Framework para realizar llamadas REST.
 *   - Se configura automáticamente con un baseUrl obtenido de las propiedades
 *     de aplicación (`customers.base-url`).
 *   - Implementa manejo de errores para evitar que fallos en el servicio externo
 *     afecten la estabilidad del servicio actual.
 *
 * Comportamiento:
 *   - Si el servicio customers responde correctamente, devuelve el nombre del cliente.
 *   - Si el servicio no responde o el cliente no existe, devuelve "Unknown".
 *
 * Mantenibilidad:
 *   - Si se amplía la información consultada del cliente, se puede extender
 *     el record interno CustomerDto.
 *   - Es recomendable configurar timeouts o un circuito de resiliencia (por ejemplo,
 *     con Resilience4j o Spring Retry) si se utiliza en producción.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.contacts.clients;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Cliente HTTP para interactuar con el microservicio de "customers".
 *
 * Proporciona métodos para obtener información básica de un cliente
 * a partir de su identificador UUID.
 */
@Component
public class CustomersClient {
    private static final Logger log = LoggerFactory.getLogger(CustomersClient.class);
    private final RestClient http;

    /**
     * Constructor del cliente.
     *
     * @param builder  Builder de RestClient inyectado por Spring Boot.
     * @param baseUrl  URL base del microservicio de clientes, configurable mediante
     *                 la propiedad `customers.base-url`. Por defecto usa http://localhost:8081.
     */
    public CustomersClient(RestClient.Builder builder,
                           @Value("${customers.base-url:http://localhost:8081}") String baseUrl) {
        // Configura el cliente HTTP con la URL base definida en las propiedades.
        this.http = builder.baseUrl(baseUrl).build();
    }

    /**
     * Obtiene el nombre de un cliente a partir de su UUID.
     *
     * @param id UUID del cliente a consultar.
     * @return Nombre del cliente si el servicio responde correctamente,
     *         o "Unknown" si ocurre un error o no existe.
     */
    public String getNameOrUnknown(UUID id) {
        try {
            // Realiza una petición GET al endpoint /customers/{id} del servicio externo.
            CustomerDto dto = http.get()
                    .uri("/customers/{id}", id)
                    .retrieve()
                    .body(CustomerDto.class);
            // Si el DTO existe y el nombre no está vacío, se devuelve; de lo contrario, "Unknown".
            return (dto != null && dto.name() != null && !dto.name().isBlank())
                    ? dto.name()
                    : "Unknown";
        } catch (RestClientException e) {
            // En caso de error (conexión fallida, timeout, etc.), se registra en logs y se devuelve "Unknown".
            log.error("customers-svc unreachable when fetching id={}: {}", id, e.getMessage());
            return "Unknown";
        }
    }

    /**
     * Record interno que representa la estructura del cliente recibida
     * desde el servicio de "customers".
     *
     * Campos:
     *   id   → Identificador UUID del cliente.
     *   name → Nombre del cliente.
     */
    public record CustomerDto(UUID id, String name) {}
}