/*
 * -----------------------------------------------------------------------------
 * ContactsApplication.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Clase principal del microservicio "contacts-svc". Es el punto de entrada
 *   de la aplicación Spring Boot encargada de la gestión de contactos asociados
 *   a clientes.
 *
 * Contexto de uso:
 *   - Inicializa el contexto de Spring Boot y levanta el servidor embebido.
 *   - Escanea los paquetes bajo `com.proyecto.ops.contacts` para registrar
 *     componentes, servicios, controladores y configuraciones.
 *
 * Diseño:
 *   - Usa la anotación @SpringBootApplication, que combina:
 *       @Configuration → Define la clase como fuente de configuración de beans.
 *       @EnableAutoConfiguration → Activa la configuración automática de Spring.
 *       @ComponentScan → Busca componentes dentro del paquete base.
 *
 * Mantenibilidad:
 *   - Normalmente no requiere cambios.
 *   - Se puede modificar para agregar configuraciones globales o listeners
 *     específicos si la aplicación lo requiere.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.contacts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal del microservicio de contactos (contacts-svc).
 *
 * Inicia la aplicación Spring Boot y configura el escaneo de componentes
 * dentro del paquete base `com.proyecto.ops.contacts`.
 */
@SpringBootApplication
public class ContactsApplication {
    /**
     * Método principal que inicia la aplicación Spring Boot.
     *
     * @param args Argumentos de línea de comandos opcionales.
     */
    public static void main(String[] args) {
        // Inicia el contexto de Spring Boot y levanta el servidor embebido (por defecto, Tomcat).
        SpringApplication.run(ContactsApplication.class, args);
    }
}