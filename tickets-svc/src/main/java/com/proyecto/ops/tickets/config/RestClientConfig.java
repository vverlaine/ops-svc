/*
 * -----------------------------------------------------------------------------
 * RestClientConfig.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Clase de configuración que define el cliente HTTP (RestClient) utilizado
 *   por el microservicio "tickets-svc" para comunicarse con el microservicio
 *   "customers-svc".
 *
 * Contexto de uso:
 *   - Se encarga de crear un bean de tipo RestClient configurado con la URL base
 *     del servicio de clientes.
 *   - Permite realizar llamadas HTTP de forma centralizada y reutilizable.
 *
 * Diseño:
 *   - Anotada con @Configuration para ser detectada automáticamente por Spring.
 *   - Define un método con @Bean que construye el cliente HTTP usando un valor
 *     configurable (propiedad `customers.base-url`).
 *   - Si la propiedad no está definida, utiliza la URL por defecto http://localhost:8081.
 *
 * Mantenibilidad:
 *   - Si se agregan más servicios externos, pueden definirse aquí nuevos métodos
 *     que creen RestClient para cada uno, con sus respectivas propiedades.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.tickets.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Clase de configuración que crea el cliente HTTP utilizado para conectar
 * el microservicio "tickets-svc" con "customers-svc".
 */
@Configuration
public class RestClientConfig {

    /**
     * Define un bean RestClient configurado con la URL base del servicio de clientes.
     *
     * @param baseUrl URL base del microservicio "customers-svc"
     *                (valor por defecto: http://localhost:8081).
     * @return Instancia configurada de RestClient lista para inyección.
     */
    @Bean
    RestClient customersRestClient(
            @Value("${customers.base-url:http://localhost:8081}") String baseUrl
    ) {
        // Construye el cliente HTTP (RestClient) con la URL base definida en la configuración.
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}