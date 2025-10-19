/*
 * -----------------------------------------------------------------------------
 * CustomersApplication.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Clase principal del microservicio "customers-svc". Es el punto de entrada
 *   de la aplicación Spring Boot encargada de gestionar la información de clientes.
 *
 * Contexto de uso:
 *   - Inicia el contexto de Spring Boot y levanta el servidor embebido (Tomcat).
 *   - Escanea automáticamente los componentes dentro del paquete
 *     `com.proyecto.ops.customers`.
 *
 * Diseño:
 *   - Anotada con @SpringBootApplication, que combina:
 *       @Configuration → Marca la clase como fuente de configuración de beans.
 *       @EnableAutoConfiguration → Activa la configuración automática de Spring.
 *       @ComponentScan → Escanea los paquetes para detectar componentes y servicios.
 *
 * Mantenibilidad:
 *   - Normalmente no requiere cambios.
 *   - Si se agregan configuraciones globales o listeners, pueden declararse aquí
 *     o en clases de configuración separadas.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.customers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal del microservicio de clientes (customers-svc).
 *
 * Inicia la aplicación Spring Boot y configura el escaneo de componentes
 * en el paquete base `com.proyecto.ops.customers`.
 */
@SpringBootApplication
public class CustomersApplication {
    /**
     * Método principal que arranca la aplicación Spring Boot.
     *
     * @param args Argumentos de línea de comandos opcionales.
     */
    public static void main(String[] args) {
        // Inicia el contexto de Spring Boot y levanta el servidor embebido.
        SpringApplication.run(CustomersApplication.class, args);
    }
}