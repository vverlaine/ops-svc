package com.proyecto.ops.customers.config;

import java.net.http.HttpClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientsConfig {

  @Bean
  public RestClient.Builder restClientBuilder() {
    return RestClient.builder();
  }

  @Bean("authRestClient")
  public RestClient authRestClient(
      RestClient.Builder builder,
      @Value("${auth.base-url:http://localhost:8099}") String baseUrl) {
    return builder
        .baseUrl(baseUrl)
        .build();
  }

  @Bean
  public HttpClient httpClient() {
    return HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .build();
  }
}