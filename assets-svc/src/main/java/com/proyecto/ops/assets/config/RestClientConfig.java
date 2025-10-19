/*
 * -----------------------------------------------------------------------------
 * RestClientConfig.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Clase de configuración de Spring que define beans de tipo RestClient
 *   para establecer conexiones HTTP hacia otros microservicios externos.
 *
 * Contexto de uso:
 *   - Este archivo provee el bean RestClient específico para comunicarse con
 *     el servicio de "customers" (clientes).
 *   - El cliente HTTP se utiliza en la clase CustomersClient.java.
 *
 * Diseño:
 *   - Utiliza la anotación @Configuration para que Spring la detecte como
 *     clase de configuración.
 *   - Declara un bean con @Bean, llamado `customersRestClient`.
 *   - Configura la URL base del servicio remoto mediante la propiedad
 *     `customers.base-url` (con valor por defecto http://localhost:8081).
 *
 * Mantenibilidad:
 *   - Si se agregan nuevos microservicios a integrar, se pueden crear nuevos
 *     métodos @Bean similares, cada uno con su base URL correspondiente.
 *   - Permite centralizar la configuración HTTP de los clientes externos.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.assets.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Clase de configuración para instanciar beans RestClient utilizados
 * para comunicarse con servicios externos.
 */
@Configuration
public class RestClientConfig {
    /**
     * Bean que construye un RestClient configurado para el servicio "customers".
     *
     * @param baseUrl URL base del servicio de clientes. Se obtiene de la propiedad
     *                `customers.base-url` en el archivo de configuración de la
     *                aplicación (por ejemplo application.yml o application.properties).
     *                Si no está definida, se utiliza el valor por defecto
     *                {@code http://localhost:8081}.
     * @return Instancia de {@link org.springframework.web.client.RestClient}
     *         lista para inyección en otros componentes.
     */
    @Bean
    RestClient customersRestClient(@Value("${customers.base-url:http://localhost:8081}") String baseUrl) {
        // Construye el cliente HTTP con la URL base configurada y lo expone como bean de Spring.
        return RestClient.builder().baseUrl(baseUrl).build();
    }
}