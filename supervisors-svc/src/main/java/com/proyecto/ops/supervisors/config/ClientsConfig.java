/*
 * -----------------------------------------------------------------------------
 * ClientsConfig.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Clase de configuración que define beans reutilizables para clientes HTTP (RestClient)
 *   utilizados dentro del microservicio "supervisors-svc".
 *
 * Contexto de uso:
 *   - Proporciona una instancia configurada del RestClient para comunicarse con
 *     el servicio de autenticación (auth-svc).
 *   - Permite inyectar el cliente HTTP mediante el calificador "authRestClient".
 *
 * Diseño:
 *   - Anotada con @Configuration para que Spring la detecte y registre sus beans.
 *   - Define el bean "authRestClient" con una URL base configurable mediante
 *     la propiedad `auth.base-url` o, por defecto, `http://localhost:8099`.
 *
 * Mantenibilidad:
 *   - Si se agregan nuevos servicios externos, pueden definirse nuevos métodos
 *     similares dentro de esta clase.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.supervisors.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Clase de configuración que registra los clientes HTTP utilizados en el servicio de técnicos.
 */
@Configuration
public class ClientsConfig {
  @Bean
  @Qualifier("authRestClient")
    /**
     * Crea un cliente HTTP (RestClient) configurado para comunicarse con el servicio de autenticación.
     *
     * @param baseUrl URL base del servicio de autenticación (por defecto: http://localhost:8099).
     * @return Instancia del RestClient configurada con la URL base.
     */
  public RestClient authRestClient(
      @Value("${auth.base-url:http://localhost:8099}") String baseUrl) {
        // Construye y devuelve un RestClient con la URL base especificada.
        return RestClient.builder().baseUrl(baseUrl).build();
  }
}