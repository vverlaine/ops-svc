/*
 * -----------------------------------------------------------------------------
 * CustomersClient.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Cliente HTTP (usando Spring RestClient) para comunicarse con el servicio
 *   de "customers" y consultar información básica de clientes.
 *
 * Contexto de uso:
 *   Este componente es consumido por el assets-svc para:
 *     - Verificar si existe un cliente por su UUID (consulta 404 vs 200).
 *     - Obtener el nombre de un cliente, devolviendo "Unknown" cuando
 *       no exista o el servicio no esté disponible.
 *
 * Diseño y decisiones clave:
 *   - Se utiliza `RestClient` (Spring Framework) con inyección de un
 *     `RestClient.Builder` y composición del `baseUrl` vía propiedad
 *     `customers.base-url` (con valor por defecto a http://localhost:8081).
 *   - Se captura específicamente `HttpClientErrorException.NotFound` para
 *     distinguir entre "no existe" (404) y otros errores de red.
 *   - En caso de errores de conectividad (`RestClientException`), por
 *     resiliencia se asume "no existe" o se devuelve "Unknown", evitando
 *     propagar excepciones en cascada al resto del servicio.
 *
 * Consideraciones operativas:
 *   - Loggea en nivel ERROR cuando el servicio de customers es inalcanzable,
 *     incluyendo el UUID e información básica del error.
 *   - Este cliente no implementa reintentos ni timeouts personalizados;
 *     estas políticas deben configurarse en el bean `RestClient.Builder`
 *     (por ejemplo, con con timeouts del `HttpClient` o interceptores).
 *
 * Seguridad:
 *   - Si el endpoint de customers requiere autenticación o headers
 *     adicionales, deben configurarse en el `RestClient.Builder` que se
 *     inyecta desde la configuración de Spring.
 *
 * Mantenibilidad:
 *   - Métodos simples y de única responsabilidad:
 *       exists(UUID): boolean
 *       getNameOrUnknown(UUID): String
 *   - Fácil de extender para exponer más operaciones del dominio "customers".
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.assets.clients;

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Cliente para el servicio de "customers".
 *
 * Provee dos operaciones:
 * <ul>
 *   <li>{@link #exists(UUID)} — Verifica si un cliente existe (true si el servicio responde 2xx).</li>
 *   <li>{@link #getNameOrUnknown(UUID)} — Obtiene el nombre del cliente o "Unknown" si no existe o hay error.</li>
 * </ul>
 *
 * El `baseUrl` se toma de la propiedad de aplicación `customers.base-url`.
 * Si no está definida, por defecto apunta a {@code http://localhost:8081}.
 */
@Component
public class CustomersClient {
    // Logger para registrar eventos de conectividad/errores con el servicio de customers.
    private static final Logger log = LoggerFactory.getLogger(CustomersClient.class);
    // Cliente HTTP de Spring ya configurado con el baseUrl del servicio de customers.
    private final RestClient http;

    /**
     * Crea el cliente HTTP para el servicio de customers.
     *
     * @param builder `RestClient.Builder` inyectado por Spring; aquí se termina de
     *                construir el cliente con el `baseUrl` correspondiente.
     * @param baseUrl URL base del servicio de customers. Se inyecta desde
     *                `application.properties`/`application.yml` bajo la clave
     *                `customers.base-url`, con valor por defecto `http://localhost:8081`.
     */
    public CustomersClient(RestClient.Builder builder,
                           @Value("${customers.base-url:http://localhost:8081}") String baseUrl) {
        this.http = builder.baseUrl(baseUrl).build();
    }

    public boolean exists(UUID id) {
        // Intenta hacer una petición GET a /customers/{id}. Si responde 2xx, el cliente existe.
        try {
            http.get().uri("/customers/{id}", id).retrieve().toBodilessEntity();
            // Respuesta 2xx: consideramos que el recurso existe.
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            // 404 Not Found: el cliente no existe en el servicio de customers.
            return false;
        } catch (RestClientException e) {
            // Cualquier otro error de cliente/servidor o de red: se registra para diagnóstico.
            log.error("customers-svc unreachable when checking id={}: {}", id, e.getMessage());
            // Por resiliencia: si el servicio está caído o inalcanzable, devolvemos "no existe".
            return false;
        }
    }

    /**
     * Obtiene el nombre del cliente desde el servicio de customers.
     *
     * @param id UUID del cliente a consultar.
     * @return El nombre del cliente si el servicio responde correctamente; en caso de 404 o
     *         cualquier error de conectividad, devuelve la cadena literal "Unknown".
     */
    public String getNameOrUnknown(UUID id) {
        // Realiza GET a /customers/{id} y espera un cuerpo JSON que contenga la clave "name".
        try {
            ResponseEntity<Map> resp = http.get().uri("/customers/{id}", id).retrieve().toEntity(Map.class);
            // Extrae el campo "name" del cuerpo (si existe); si no, se asumirá "Unknown".
            Object name = resp.getBody() != null ? resp.getBody().get("name") : null;
            // Devuelve el nombre como String cuando está presente; si no, "Unknown".
            return name != null ? String.valueOf(name) : "Unknown";
        } catch (HttpClientErrorException.NotFound e) {
            // 404 Not Found: el cliente no existe; regresamos "Unknown".
            return "Unknown";
        } catch (RestClientException e) {
            // Error de conectividad u otro error del cliente HTTP; registramos y protegemos al consumidor.
            log.error("customers-svc unreachable when fetching id={}: {}", id, e.getMessage());
            // Respuesta segura por defecto para no romper el flujo de negocio.
            return "Unknown";
        }
    }
}