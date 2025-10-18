package com.proyecto.ops.tickets.clients;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
public class CustomersClient {

    private final RestClient http;

    public CustomersClient(RestClient.Builder builder,
                           @Value("${customers.base-url:http://localhost:8081}") String baseUrl) {
        this.http = builder.baseUrl(baseUrl).build();
    }

    /** Ya lo tenías: comprobar existencia */
    public boolean exists(UUID id) {
        try {
            http.get().uri("/customers/{id}", id).retrieve().toBodilessEntity();
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        }
    }

    /* ========= NUEVO: DTO mínimo + helpers ========= */

    /** DTO chiquito que coincide con lo que expone customers-svc */
    public static record CustomerMini(UUID id, String name) {}

    /** Obtener el cliente mínimo (200) o lanzar excepción si 404/4xx */
    public CustomerMini getMini(UUID id) {
        return http.get()
                .uri("/customers/{id}", id)
                .retrieve()
                .body(CustomerMini.class);
    }

    /** Intentar obtener el nombre sin romper el flujo si falla */
    public String tryGetName(UUID id) {
        try {
            CustomerMini c = getMini(id);
            return c != null ? c.name() : null;
        } catch (HttpClientErrorException e) {
            // 4xx -> lo tratamos como “no disponible”
            return null;
        } catch (Exception e) {
            // timeouts / refused / etc. -> no bloquea
            return null;
        }
    }
}