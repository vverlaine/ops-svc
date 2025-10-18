package com.proyecto.ops.technicians.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientsConfig {
  @Bean
  @Qualifier("authRestClient")
  public RestClient authRestClient(
      @Value("${auth.base-url:http://localhost:8099}") String baseUrl) {
    return RestClient.builder().baseUrl(baseUrl).build();
  }
}