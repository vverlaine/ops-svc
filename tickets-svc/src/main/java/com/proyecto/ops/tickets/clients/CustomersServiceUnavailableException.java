/*
 * -----------------------------------------------------------------------------
 * CustomersServiceUnavailableException.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Excepción personalizada que indica que el microservicio "customers-svc"
 *   no está disponible o no pudo responder correctamente durante una llamada HTTP.
 *
 * Contexto de uso:
 *   - Pertenece al microservicio "tickets-svc".
 *   - Se lanza cuando ocurre un error de comunicación (por ejemplo, un timeout o fallo de conexión)
 *     al intentar consultar el servicio de clientes mediante la clase CustomersClient.
 *
 * Diseño:
 *   - Extiende RuntimeException para evitar la obligación de captura (checked exception).
 *   - Permite incluir un mensaje descriptivo y la causa original del error.
 *
 * Ejemplo de uso:
 *   throw new CustomersServiceUnavailableException("No se pudo conectar al servicio de clientes", e);
 *
 * Mantenibilidad:
 *   - Si se agregan más servicios externos, se pueden definir excepciones similares
 *     siguiendo este mismo patrón para cada cliente.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.tickets.clients;

/**
 * Excepción que indica que el servicio de clientes (customers-svc)
 * no está disponible o falló durante la comunicación HTTP.
 */
public class CustomersServiceUnavailableException extends RuntimeException {
    /**
     * Constructor que permite crear la excepción con un mensaje descriptivo y la causa original.
     *
     * @param message Descripción del error.
     * @param cause   Excepción original que provocó este error.
     */
    public CustomersServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}