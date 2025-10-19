/*
 * -----------------------------------------------------------------------------
 * WorkOrdersApplication.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Clase principal de arranque del microservicio "work-orders-svc".
 *
 * Contexto de uso:
 *   - Actúa como punto de entrada de la aplicación Spring Boot.
 *   - Inicializa el contexto de Spring, carga los beans configurados
 *     y arranca los componentes necesarios para ejecutar el servicio.
 *
 * Diseño:
 *   - Anotada con @SpringBootApplication, que agrupa:
 *       • @Configuration → Define configuraciones y beans.
 *       • @EnableAutoConfiguration → Configura automáticamente Spring.
 *       • @ComponentScan → Escanea el paquete base com.proyecto.ops.workorders.
 *
 * Ejecución:
 *   - Puede ejecutarse desde:
 *       • Línea de comandos: mvn spring-boot:run
 *       • JAR compilado: java -jar work-orders-svc.jar
 *
 * Mantenibilidad:
 *   - Esta clase solo debe modificarse si cambia el paquete raíz
 *     o la estructura general del proyecto.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.workorders;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
/**
 * Clase principal que inicia la aplicación Spring Boot del microservicio "work-orders-svc".
 */
public class WorkOrdersApplication {
    /**
     * Método principal que lanza el microservicio "work-orders-svc".
     *
     * @param args Argumentos pasados desde la línea de comandos.
     */
    public static void main(String[] args) {
        SpringApplication.run(WorkOrdersApplication.class, args);
    }
}