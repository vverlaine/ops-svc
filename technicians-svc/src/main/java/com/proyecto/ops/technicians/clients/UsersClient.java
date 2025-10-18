package com.proyecto.ops.technicians.clients;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class UsersClient {
  private static final Logger log = LoggerFactory.getLogger(UsersClient.class);
  private final RestClient http;

  public UsersClient(RestClient.Builder builder,
                     @Value("${users.base-url:http://localhost:8081}") String baseUrl) {
    this.http = builder.baseUrl(baseUrl).build();
  }

  public String getNameOrUnknown(UUID id) {
    if (id == null) return null;
    try {
      UserDto dto = http.get().uri("/users/{id}", id).retrieve().body(UserDto.class);
      return (dto != null && dto.name != null && !dto.name.isBlank()) ? dto.name : "Unknown";
    } catch (RestClientException e) {
      log.error("users-svc unreachable id={}: {}", id, e.getMessage());
      return "Unknown";
    }
  }

  public static class UserDto { public UUID id; public String name; }
}