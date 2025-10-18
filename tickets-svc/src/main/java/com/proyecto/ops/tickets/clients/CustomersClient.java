package com.proyecto.ops.tickets.clients;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class CustomersClient {

    private final RestClient http;

    public CustomersClient(
            RestClient.Builder builder,
            @Value("${customers.base-url:http://localhost:8081}") String baseUrl) {
        this.http = builder.baseUrl(baseUrl).build();
    }

    /** Verifica si existe el customer en customers-svc. */
    public boolean exists(UUID id) {
        try {
            http.get()
                .uri("/customers/{id}", id)
                .retrieve()
                .toBodilessEntity(); // 200 si existe
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            return false; // 404 -> no existe
        } catch (RestClientException e) {
            // timeouts/5xx/etc. Lo tomamos como "no disponible/no existe" para no romper el flujo.
            return false;
        }
    }

    /** DTO mínimo para leer name del customers-svc */
    private static record CustomerDto(UUID id, String name) {}

    /** Intenta obtener el nombre del cliente; si falla, devuelve null. */
    public String tryGetName(UUID id) {
        try {
            CustomerDto dto = http.get()
                    .uri("/customers/{id}", id)
                    .retrieve()
                    .body(CustomerDto.class);
            return dto != null ? dto.name() : null;
        } catch (HttpClientErrorException.NotFound e) {
            return null; // 404
        } catch (RestClientException e) {
            return null; // timeouts/5xx/etc
        }
    }
}