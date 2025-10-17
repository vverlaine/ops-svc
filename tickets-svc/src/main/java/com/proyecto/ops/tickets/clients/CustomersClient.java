package com.proyecto.ops.tickets.clients;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
public class CustomersClient {
  private final RestClient http;

  public CustomersClient(
      RestClient.Builder builder,
      @Value("${customers.base-url:http://localhost:8081}") String baseUrl
  ) {
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
    }
  }
}