package com.proyecto.ops.contacts.clients;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
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

    public String getNameOrUnknown(UUID id) {
        try {
            CustomerDto dto = http.get()
                    .uri("/customers/{id}", id)
                    .retrieve()
                    .body(CustomerDto.class);
            return (dto != null && dto.name() != null && !dto.name().isBlank())
                    ? dto.name()
                    : "Unknown";
        } catch (RestClientException e) {
            log.error("customers-svc unreachable when fetching id={}: {}", id, e.getMessage());
            return "Unknown";
        }
    }

    public record CustomerDto(UUID id, String name) {}
}