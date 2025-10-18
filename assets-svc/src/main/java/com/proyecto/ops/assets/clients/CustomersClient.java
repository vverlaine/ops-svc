package com.proyecto.ops.assets.clients;

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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
            http.get().uri("/customers/{id}", id).retrieve().toBodilessEntity();
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        } catch (RestClientException e) {
            log.error("customers-svc unreachable when checking id={}: {}", id, e.getMessage());
            // Por resiliencia, considera que no existe si no responde
            return false;
        }
    }

    public String getNameOrUnknown(UUID id) {
        try {
            ResponseEntity<Map> resp = http.get().uri("/customers/{id}", id).retrieve().toEntity(Map.class);
            Object name = resp.getBody() != null ? resp.getBody().get("name") : null;
            return name != null ? String.valueOf(name) : "Unknown";
        } catch (HttpClientErrorException.NotFound e) {
            return "Unknown";
        } catch (RestClientException e) {
            log.error("customers-svc unreachable when fetching id={}: {}", id, e.getMessage());
            return "Unknown";
        }
    }
}