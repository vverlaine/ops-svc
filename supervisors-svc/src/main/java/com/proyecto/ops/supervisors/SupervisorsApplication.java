/*
 * -----------------------------------------------------------------------------
 * SupervisorsApplication.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Clase principal del microservicio "supervisors-svc". Sirve como punto de
 *   entrada para iniciar la aplicación Spring Boot que gestiona la lógica
 *   relacionada con los supervisores del sistema.
 *
 * Contexto de uso:
 *   - Inicializa el contexto de Spring Boot.
 *   - Escanea los componentes dentro del paquete base `com.proyecto.ops.supervisors`.
 *   - Levanta el servidor embebido (Tomcat por defecto).
 *
 * Diseño:
 *   - Anotada con @SpringBootApplication, que combina:
 *       • @Configuration → Permite definir beans y configuraciones.
 *       • @EnableAutoConfiguration → Activa la configuración automática de Spring.
 *       • @ComponentScan → Escanea los paquetes para registrar componentes, servicios y controladores.
 *
 * Mantenibilidad:
 *   - Normalmente no requiere cambios, salvo que se necesiten configuraciones
 *     globales o inicializaciones personalizadas.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.supervisors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal del microservicio de supervisores (supervisors-svc).
 *
 * Inicia la aplicación Spring Boot y carga el contexto de la aplicación.
 */
@SpringBootApplication
public class SupervisorsApplication {
    /**
     * Método principal que ejecuta la aplicación Spring Boot.
     *
     * @param args Argumentos de línea de comandos opcionales.
     */
    public static void main(String[] args) {
        // Inicia el contexto de Spring Boot y levanta el servidor embebido.
        SpringApplication.run(SupervisorsApplication.class, args);
    }
}
