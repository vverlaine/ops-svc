package com.proyecto.ops.tickets.clients;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

@Component
public class CustomersClient {

    private static final Logger log = LoggerFactory.getLogger(CustomersClient.class);
    private final RestClient http;

    // Inyecta el bean definido en RestClientConfig (ya existente)
    public CustomersClient(RestClient customersRestClient) {
        this.http = customersRestClient;
    }

    // DTO para mapear la respuesta de /customers/{id}
    public static record CustomerDTO(UUID id, String name) {}

    /** Verifica existencia del cliente (200 -> true, 404 -> false). */
    public boolean exists(UUID id) {
        try {
            http.get()
                .uri("/customers/{id}", id)
                .retrieve()
                .toBodilessEntity();
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        } catch (RestClientException e) {
            log.error("customers-svc unreachable when checking id={}: {}", id, e.getMessage());
            // En fallo de red, devolvemos false para no dejar crear el ticket
            return false;
        }
    }

    /** Devuelve el nombre del cliente; si no se puede, "Unknown". */
    public String tryGetName(UUID id) {
        try {
            CustomerDTO dto = http.get()
                .uri("/customers/{id}", id)
                .retrieve()
                .body(CustomerDTO.class);

            return (dto != null && dto.name() != null) ? dto.name() : "Unknown";
        } catch (HttpClientErrorException.NotFound e) {
            return "Unknown";
        } catch (ResourceAccessException e) {
            log.error("customers-svc unreachable when fetching id={}: {}", id, e.getMessage());
            return "Unknown";
        } catch (RestClientException e) {
            log.error("Error calling customers-svc for id={}: {}", id, e.getMessage());
            return "Unknown";
        }
    }
}