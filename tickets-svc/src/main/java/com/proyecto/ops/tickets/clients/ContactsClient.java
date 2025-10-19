/*
 * -----------------------------------------------------------------------------
 * ContactsClient.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Cliente HTTP que permite la comunicación con el microservicio "contacts-svc"
 *   para obtener información de contactos registrados.
 *
 * Contexto de uso:
 *   - Pertenece al microservicio "tickets-svc".
 *   - Se utiliza principalmente para recuperar el nombre del contacto asociado
 *     a un ticket o entidad relacionada mediante su UUID.
 *
 * Diseño:
 *   - Anotado con @Component para ser administrado por el contenedor de Spring.
 *   - Usa RestClient (Spring Framework 6) para realizar peticiones HTTP.
 *   - La URL base se define por la propiedad `contacts.base-url` o, si no existe,
 *     se usa el valor por defecto `http://localhost:8089`.
 *   - Implementa manejo de errores mediante logs en caso de que el servicio
 *     "contacts-svc" no esté disponible.
 *
 * Método principal:
 *   - getNameOrUnknown(UUID id)
 *       → Devuelve el nombre del contacto si se encuentra,
 *         o "Unknown" si no existe o no puede consultarse.
 *
 * Mantenibilidad:
 *   - Si el servicio de contactos amplía su API, pueden agregarse nuevos métodos
 *     aquí para consumir los endpoints adicionales.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.tickets.clients;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Cliente HTTP para interactuar con el microservicio "contacts-svc".
 *
 * Permite obtener información básica de contactos mediante su UUID.
 */
@Component
public class ContactsClient {
    private static final Logger log = LoggerFactory.getLogger(ContactsClient.class);
    private final RestClient http;

    /**
     * Constructor que inicializa el cliente HTTP con la URL base configurada.
     *
     * @param builder Builder proporcionado por Spring para construir el RestClient.
     * @param baseUrl URL base del servicio de contactos (por defecto: http://localhost:8089).
     */
    public ContactsClient(RestClient.Builder builder,
                          @Value("${contacts.base-url:http://localhost:8089}") String baseUrl) {
        // Construye el cliente HTTP con la URL base definida en la configuración.
        this.http = builder.baseUrl(baseUrl).build();
    }

    /**
     * Obtiene el nombre del contacto a partir de su identificador UUID.
     *
     * @param id Identificador único del contacto.
     * @return Nombre del contacto si se encuentra, o "Unknown" si no existe o hay error de conexión.
     */
    public String getNameOrUnknown(UUID id) {
        // Si el identificador es nulo, no se realiza la consulta.
        if (id == null) return null;
        try {
            // Realiza la petición GET al endpoint /contacts/{id} y mapea la respuesta al DTO interno.
            ContactDto dto = http.get()
                    .uri("/contacts/{id}", id)
                    .retrieve()
                    .body(ContactDto.class);
            // Retorna el nombre del contacto si está disponible, de lo contrario devuelve "Unknown".
            return (dto != null && dto.name != null && !dto.name.isBlank())
                    ? dto.name
                    : "Unknown";
        } catch (RestClientException e) {
            // Captura excepciones de conexión o errores del servicio remoto y registra el fallo.
            log.error("contacts-svc unreachable when fetching id={}: {}", id, e.getMessage());
            return "Unknown";
        }
    }

    /**
     * Clase interna que representa la estructura básica del objeto Contacto
     * devuelto por el microservicio "contacts-svc".
     */
    public static class ContactDto {
        // Identificador único del contacto (UUID).
        public UUID id;
        // Nombre del contacto asociado.
        public String name;
    }
}