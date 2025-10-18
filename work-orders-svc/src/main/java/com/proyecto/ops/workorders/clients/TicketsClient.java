package com.proyecto.ops.workorders.clients;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class TicketsClient {
  private static final Logger log = LoggerFactory.getLogger(TicketsClient.class);
  private final RestClient http;

  public TicketsClient(RestClient.Builder builder,
                       @Value("${tickets.base-url:http://localhost:8080}") String baseUrl) {
    this.http = builder.baseUrl(baseUrl).build();
  }

  public boolean exists(UUID id) {
    try {
      HttpStatusCode code = http.get().uri("/tickets/{id}", id)
          .exchange((req, res) -> res.getStatusCode());
      return code.is2xxSuccessful();
    } catch (RestClientException e) {
      log.error("tickets-svc unreachable when fetching id={}: {}", id, e.getMessage());
      throw e; // lo manejamos en el controller como 503
    }
  }
}