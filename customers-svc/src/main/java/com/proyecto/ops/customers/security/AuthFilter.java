/*
 * -----------------------------------------------------------------------------
 * AuthFilter.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Filtro de autenticación que intercepta cada petición HTTP y valida el token
 *   de autorización delegando la verificación en el microservicio de autenticación
 *   (`auth-svc`). Si la validación es exitosa, inyecta un objeto
 *   {@link com.proyecto.ops.customers.security.AuthenticatedUser} en la request.
 *
 * Contexto de uso:
 *   - Forma parte del microservicio "customers-svc".
 *   - Protege los endpoints del API asegurando que solo peticiones con un token
 *     válido puedan acceder.
 *   - Las rutas públicas (definidas en PUBLIC) no requieren autenticación.
 *
 * Diseño:
 *   - Extiende OncePerRequestFilter para asegurar una única ejecución por solicitud.
 *   - Utiliza HttpClient estándar de Java para comunicarse con el auth-svc.
 *   - Realiza una solicitud GET al endpoint `/auth/me` para validar el token.
 *   - Extrae los datos del usuario autenticado de la respuesta JSON.
 *
 * Mantenibilidad:
 *   - Se puede extender para manejar caché de tokens o validaciones adicionales.
 *   - Es recomendable reemplazar el parseo manual de JSON por una librería como Jackson.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.customers.security;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro que valida tokens de autenticación en cada petición entrante.
 *
 * Se comunica con el servicio externo `auth-svc` para validar el token Bearer
 * y, en caso exitoso, adjunta el usuario autenticado a la solicitud.
 */
@Component
public class AuthFilter extends OncePerRequestFilter {

    // Rutas públicas que no requieren autenticación.
    private static final Set<String> PUBLIC = Set.of(
            "/actuator/health", "/actuator/info", "/error"
    );

    // Cliente HTTP reutilizable para enviar solicitudes al servicio de autenticación.
    private final HttpClient http;

    /**
     * @param http cliente HTTP reutilizable para consultar el servicio de autenticación.
     */
    public AuthFilter(HttpClient http) {
        this.http = http;
    }

    /**
     * Intercepta la petición, permite el paso si la ruta es pública y en las
     * demás valida el token Bearer llamando al endpoint `/auth/me`. Cuando la
     * validación es exitosa, adjunta un {@link AuthenticatedUser} a la request.
     *
     * @param req   petición HTTP entrante.
     * @param res   respuesta HTTP que se enviará al cliente.
     * @param chain cadena de filtros que continúa el procesamiento.
     * @throws ServletException si otro filtro lanza la excepción.
     * @throws IOException      si ocurre un error de E/S al gestionar la petición.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        // Obtiene la ruta solicitada para determinar si requiere autenticación.
        final String path = req.getRequestURI();

        // Si la ruta es pública, se deja pasar la petición sin validación de token.
        if (PUBLIC.stream().anyMatch(path::startsWith)) {
            chain.doFilter(req, res);
            return;
        }

        // Obtiene el encabezado Authorization: Bearer <token>.
        String auth = req.getHeader(HttpHeaders.AUTHORIZATION);
        // Si no existe el encabezado o no tiene formato Bearer, se rechaza la solicitud.
        if (auth == null || !auth.startsWith("Bearer ")) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json;charset=ISO-8859-1");
            res.getWriter().write("{\"error\":\"Unauthorized\"}");
            return;
        }

        // Valida con auth-svc /auth/me
        try {
            // Construye la solicitud HTTP GET hacia el endpoint /auth/me del auth-svc.
            HttpRequest meReq = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8099/auth/me"))
                    .header(HttpHeaders.AUTHORIZATION, auth)
                    .GET()
                    .build();

            // Envía la solicitud y obtiene la respuesta del servicio de autenticación.
            HttpResponse<String> meRes = http.send(meReq, HttpResponse.BodyHandlers.ofString());

            // Si la respuesta no es 200 OK, se considera el token inválido o expirado.
            if (meRes.statusCode() != 200) {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.setContentType("application/json;charset=ISO-8859-1");
                res.getWriter().write("{\"error\":\"Unauthorized\"}");
                return;
            }

            // Extrae manualmente los valores del JSON devuelto por /auth/me.
            // Parseo súper simple sin libs extra (id, name, email, role)
            // Espera JSON: {"id":"...","name":"...","email":"...","role":"..."}
            String body = meRes.body();

            String id    = extract(body, "\"id\":\"", "\"");
            String name  = extract(body, "\"name\":\"", "\"");
            String email = extract(body, "\"email\":\"", "\"");
            String role  = extract(body, "\"role\":\"", "\"");

            // Crea el objeto AuthenticatedUser con los datos obtenidos del servicio de autenticación.
            AuthenticatedUser me = new AuthenticatedUser(UUID.fromString(id), name, email, role);

            // Inyecta el usuario autenticado en el request para uso posterior en controladores.
            req.setAttribute(CurrentUserArgumentResolver.REQ_ATTR, me);

            chain.doFilter(req, res);
        } catch (Exception e) {
            // Maneja cualquier error de conexión o parseo devolviendo 401 Unauthorized.
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json;charset=ISO-8859-1");
            res.getWriter().write("{\"error\":\"Unauthorized\"}");
        }
    }

    /**
     * Extrae un valor de texto de un cuerpo JSON simple buscando delimitadores específicos.
     * Este método evita dependencias externas para parseo de JSON.
     *
     * @param src   Cuerpo completo recibido del servicio de autenticación.
     * @param start Texto delimitador inicial.
     * @param end   Texto delimitador final.
     * @return Valor extraído o null si no se encuentra.
     */
    private static String extract(String src, String start, String end) {
        int s = src.indexOf(start);
        if (s < 0) return null;
        s += start.length();
        int e = src.indexOf(end, s);
        if (e < 0) return null;
        return src.substring(s, e);
    }
}
