/*
 * -----------------------------------------------------------------------------
 * ClientsConfig.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Clase de configuración que define beans relacionados con clientes HTTP
 *   utilizados dentro del microservicio "customers-svc".
 *
 * Contexto de uso:
 *   - Se utiliza para centralizar la creación y configuración de componentes
 *     compartidos como RestClient y HttpClient.
 *   - Permite que otros componentes inyecten clientes HTTP preconfigurados
 *     mediante Spring.
 *
 * Diseño:
 *   - Anotada con @Configuration para indicar que provee beans al contenedor Spring.
 *   - Define:
 *       • Un builder genérico de RestClient (para clientes REST).
 *       • Un RestClient específico para el servicio de autenticación (authRestClient).
 *       • Un HttpClient estándar de Java configurado para HTTP 1.1.
 *
 * Mantenibilidad:
 *   - Los valores como `auth.base-url` se parametrizan mediante application.yml.
 *   - Se pueden agregar más beans para otros servicios externos (por ejemplo, assets, visits, etc.).
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.customers.config;

import java.net.http.HttpClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Clase de configuración que registra beans de clientes HTTP (RestClient y HttpClient).
 */
@Configuration
public class ClientsConfig {

  /**
   * Bean que provee un builder base de RestClient.
   *
   * @return Instancia genérica de {@link org.springframework.web.client.RestClient.Builder}
   *         para crear clientes REST configurados.
   */
  @Bean
  public RestClient.Builder restClientBuilder() {
    return RestClient.builder();
  }

  /**
   * Bean que construye un cliente REST específico para el servicio de autenticación.
   *
   * @param builder Builder inyectado del RestClient base.
   * @param baseUrl URL base del servicio de autenticación, configurable mediante
   *                la propiedad `auth.base-url`. Por defecto usa http://localhost:8099.
   * @return Cliente {@link org.springframework.web.client.RestClient} configurado con la URL base.
   */
  @Bean("authRestClient")
  public RestClient authRestClient(
      RestClient.Builder builder,
      @Value("${auth.base-url:http://localhost:8099}") String baseUrl) {
    // Construye un cliente REST apuntando al servicio de autenticación.
    return builder
        .baseUrl(baseUrl)
        .build();
  }

  /**
   * Bean que provee un cliente HTTP de bajo nivel basado en {@link java.net.http.HttpClient}.
   *
   * @return Instancia de HttpClient configurada con la versión HTTP 1.1.
   */
  @Bean
  public HttpClient httpClient() {
    return HttpClient.newBuilder()
        // Se fuerza el uso de HTTP/1.1 para compatibilidad con la mayoría de APIs REST.
        .version(HttpClient.Version.HTTP_1_1)
        .build();
  }
}