/*
 * -----------------------------------------------------------------------------
 * VisitsServiceApplication.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Clase principal de arranque del microservicio "visits-svc".
 *
 * Contexto de uso:
 *   - Es el punto de entrada de la aplicación Spring Boot.
 *   - Inicializa el contexto de Spring y arranca todos los componentes definidos
 *     en el paquete `com.visits` y sus subpaquetes.
 *
 * Diseño:
 *   - Anotada con @SpringBootApplication, que combina las anotaciones:
 *       • @Configuration → Permite definir beans en la aplicación.
 *       • @EnableAutoConfiguration → Configura automáticamente los componentes de Spring.
 *       • @ComponentScan → Escanea los paquetes para detectar componentes.
 *
 * Ejecución:
 *   - Puede ejecutarse desde la línea de comandos con:
 *       `mvn spring-boot:run`
 *     o generando el archivo JAR ejecutable y usando:
 *       `java -jar visits-svc.jar`
 *
 * Mantenibilidad:
 *   - Esta clase no contiene lógica adicional y solo debe modificarse
 *     si cambia el paquete base o la estructura del proyecto.
 * -----------------------------------------------------------------------------
 */
package com.visits;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal que inicia la aplicación Spring Boot del microservicio "visits-svc".
 */
@SpringBootApplication
public class VisitsServiceApplication {
    /**
     * Método principal que lanza el microservicio "visits-svc".
     *
     * @param args Argumentos pasados desde la línea de comandos.
     */
    public static void main(String[] args) {
        SpringApplication.run(VisitsServiceApplication.class, args);
    }
}