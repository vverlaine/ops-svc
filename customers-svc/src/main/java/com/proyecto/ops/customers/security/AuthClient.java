package com.proyecto.ops.customers.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AuthClient {

  private final RestClient client;

  public AuthClient(@Qualifier("authRestClient") RestClient client) {
    this.client = client;
  }

  public String me(String bearerToken) {
    return client.get()
        .uri("/auth/me")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
        .retrieve()
        .body(String.class);
  }
}