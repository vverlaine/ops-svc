/*
 * -----------------------------------------------------------------------------
 * AssetsApplication.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Clase principal del microservicio "assets-svc". Es el punto de entrada
 *   para iniciar la aplicación Spring Boot.
 *
 * Contexto de uso:
 *   - Inicializa el contexto de Spring Boot y arranca el servidor embebido.
 *   - Escanea automáticamente los paquetes bajo `com.proyecto.ops.assets`
 *     para registrar beans, configuraciones y componentes.
 *
 * Diseño:
 *   - Utiliza la anotación @SpringBootApplication, que combina:
 *       @Configuration → Marca la clase como fuente de definiciones de beans.
 *       @EnableAutoConfiguration → Habilita la configuración automática de Spring.
 *       @ComponentScan → Escanea los paquetes para detectar componentes.
 *   - El método main invoca a `SpringApplication.run()`, que inicia el ciclo
 *     de vida del contenedor de Spring Boot.
 *
 * Mantenibilidad:
 *   - Este archivo raramente necesita cambios; sirve como punto de entrada
 *     estándar de la aplicación.
 *   - Si se agregan configuraciones globales o listeners, pueden declararse
 *     aquí o en clases separadas de configuración.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.assets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal del servicio de gestión de activos (assets-svc).
 *
 * Inicia la aplicación Spring Boot y configura el escaneo de componentes
 * en el paquete base `com.proyecto.ops.assets`.
 */
@SpringBootApplication
public class AssetsApplication {
    /**
     * Método principal que inicia la aplicación Spring Boot.
     *
     * @param args Argumentos de línea de comandos opcionales.
     *             Se pueden utilizar para personalizar el entorno o
     *             pasar parámetros específicos en el arranque.
     */
    public static void main(String[] args) {
        // Inicia el contexto de Spring Boot y levanta el servidor embebido (Tomcat/Jetty).
        SpringApplication.run(AssetsApplication.class, args);
    }
}