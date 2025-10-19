/*
 * -----------------------------------------------------------------------------
 * CorsConfig.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Configuración global de CORS (Cross-Origin Resource Sharing) para el
 *   microservicio "tickets-svc".
 *
 * Contexto de uso:
 *   - Permite que aplicaciones front-end (por ejemplo, React o Vue) puedan
 *     comunicarse con este microservicio desde dominios distintos.
 *   - Es especialmente útil en entornos locales de desarrollo (puertos 5173 y 3000).
 *
 * Diseño:
 *   - Anotada con @Configuration para que Spring la detecte automáticamente.
 *   - Expone un bean WebMvcConfigurer que define las reglas de CORS.
 *   - Permite:
 *       • Rutas: todas (`/**`)
 *       • Orígenes permitidos: `http://localhost:5173` y `http://localhost:3000`
 *       • Métodos permitidos: GET, POST, PATCH, PUT, DELETE, OPTIONS
 *       • Headers: todos (`*`)
 *       • Envío de credenciales (cookies, tokens): habilitado
 *
 * Mantenibilidad:
 *   - En producción, se recomienda limitar los orígenes a los dominios reales
 *     del front-end para mejorar la seguridad.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.tickets.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración global de CORS para el microservicio "tickets-svc".
 *
 * Define los orígenes, métodos y cabeceras permitidas para solicitudes HTTP cruzadas.
 */
@Configuration
public class CorsConfig {

    @Bean
    /**
     * Define un bean WebMvcConfigurer que configura las reglas de CORS.
     *
     * @return Configuración de CORS aplicada a todas las rutas del servicio.
     */
    public WebMvcConfigurer corsConfigurer() {
        // Retorna una instancia anónima de WebMvcConfigurer con la configuración de CORS personalizada.
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Aplica la configuración de CORS a todas las rutas del servicio.
                registry.addMapping("/**")
                        // Define los orígenes permitidos (puertos de desarrollo locales).
                        .allowedOrigins(
                                "http://localhost:5173",
                                "http://localhost:3000"
                        )
                        // Permite los métodos HTTP más comunes.
                        .allowedMethods("GET","POST","PATCH","PUT","DELETE","OPTIONS")
                        // Permite todas las cabeceras en las solicitudes.
                        .allowedHeaders("*")
                        // Habilita el envío de credenciales (por ejemplo, cookies o tokens JWT).
                        .allowCredentials(true);
            }
        };
    }
}