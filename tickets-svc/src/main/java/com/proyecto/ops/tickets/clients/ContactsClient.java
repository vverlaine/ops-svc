package com.proyecto.ops.tickets.clients;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ContactsClient {
    private static final Logger log = LoggerFactory.getLogger(ContactsClient.class);
    private final RestClient http;

    public ContactsClient(RestClient.Builder builder,
                          @Value("${contacts.base-url:http://localhost:8089}") String baseUrl) {
        this.http = builder.baseUrl(baseUrl).build();
    }

    public String getNameOrUnknown(UUID id) {
        if (id == null) return null;
        try {
            ContactDto dto = http.get()
                    .uri("/contacts/{id}", id)
                    .retrieve()
                    .body(ContactDto.class);
            return (dto != null && dto.name != null && !dto.name.isBlank())
                    ? dto.name
                    : "Unknown";
        } catch (RestClientException e) {
            log.error("contacts-svc unreachable when fetching id={}: {}", id, e.getMessage());
            return "Unknown";
        }
    }

    

    public static class ContactDto {
        public UUID id;
        public String name;
    }
}