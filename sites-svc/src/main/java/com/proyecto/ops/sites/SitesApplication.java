/*
 * -----------------------------------------------------------------------------
 * SitesApplication.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Clase principal del microservicio "sites-svc". Sirve como punto de entrada
 *   para iniciar la aplicación Spring Boot encargada de la gestión de sitios
 *   o ubicaciones asociadas a clientes.
 *
 * Contexto de uso:
 *   - Inicializa el contexto de Spring Boot.
 *   - Escanea los componentes dentro del paquete `com.proyecto.ops.sites`.
 *   - Levanta el servidor embebido (Tomcat por defecto).
 *
 * Diseño:
 *   - Anotada con @SpringBootApplication, que combina:
 *       @Configuration → Indica que la clase puede definir beans.
 *       @EnableAutoConfiguration → Activa la configuración automática de Spring.
 *       @ComponentScan → Escanea los paquetes base para registrar componentes.
 *
 * Mantenibilidad:
 *   - Generalmente no requiere cambios.
 *   - Si se agregan configuraciones globales o listeners, pueden definirse aquí
 *     o en clases separadas.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.sites;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal del microservicio de sitios (sites-svc).
 *
 * Inicia la aplicación Spring Boot y configura el escaneo de componentes
 * dentro del paquete base `com.proyecto.ops.sites`.
 */
@SpringBootApplication
public class SitesApplication {
    /**
     * Método principal que inicia la aplicación Spring Boot.
     *
     * @param args Argumentos de línea de comandos opcionales.
     */
    public static void main(String[] args) {
        // Inicia el contexto de Spring Boot y levanta el servidor embebido (Tomcat por defecto).
        SpringApplication.run(SitesApplication.class, args);
    }
}