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

@Component
public class CustomersClient {
    private static final Logger log = LoggerFactory.getLogger(CustomersClient.class);
    private final RestClient http;

    public CustomersClient(RestClient.Builder builder,
                           @Value("${customers.base-url:http://localhost:8081}") String baseUrl) {
        this.http = builder.baseUrl(baseUrl).build();
    }

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
            // si el servicio está caído, no bloquees la creación del ticket
            log.warn("customers-svc unreachable when checking id={}: {}", id, e.getMessage());
            return true;
        }
    }

    public String getNameOrUnknown(UUID id) {
        try {
            var dto = http.get()
                    .uri("/customers/{id}", id)
                    .retrieve()
                    .body(CustomerDto.class);
            return (dto != null && dto.name != null && !dto.name.isBlank()) ? dto.name : "Unknown";
        } catch (RestClientException e) {
            log.error("customers-svc unreachable when fetching id={}: {}", id, e.getMessage());
            return "Unknown";
        }
    }

    public static class CustomerDto { public UUID id; public String name; }
}