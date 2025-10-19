/*
 * -----------------------------------------------------------------------------
 * CurrentUserArgumentResolver.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Clase que implementa un resolvedor de argumentos personalizado para inyectar
 *   automáticamente el usuario autenticado en los métodos de los controladores.
 *
 * Contexto de uso:
 *   - Forma parte del microservicio "technicians-svc".
 *   - Se utiliza junto con la anotación @CurrentUser para obtener el usuario
 *     autenticado desde el atributo HTTP almacenado por el filtro AuthFilter.
 *
 * Diseño:
 *   - Anotada con @Component para su detección automática por Spring.
 *   - Implementa la interfaz HandlerMethodArgumentResolver de Spring MVC.
 *   - Verifica si un parámetro de método está anotado con @CurrentUser y
 *     si su tipo es AuthenticatedUser.
 *   - Recupera el objeto AuthenticatedUser desde el request actual.
 *
 * Flujo:
 *   1. El AuthFilter valida el token y guarda el usuario autenticado en el request.
 *   2. Este resolver intercepta parámetros anotados con @CurrentUser.
 *   3. Inyecta automáticamente el AuthenticatedUser correspondiente en el método.
 *
 * Mantenibilidad:
 *   - Si se modifica el nombre del atributo de request, también debe actualizarse
 *     la constante REQ_ATTR aquí y en el AuthFilter.
 *   - Puede extenderse para validar permisos o roles antes de resolver el argumento.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.technicians.security;

import java.util.Objects;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Resolvedor de argumentos personalizado para la anotación @CurrentUser.
 *
 * Permite inyectar el usuario autenticado en los métodos de los controladores.
 */
@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {
    // Nombre del atributo HTTP donde se almacena el usuario autenticado.
    public static final String REQ_ATTR = "AUTH_USER";

    /**
     * Determina si este resolvedor aplica al parámetro analizado.
     *
     * @param parameter Parámetro del método que se está evaluando.
     * @return true si el parámetro está anotado con @CurrentUser y es de tipo AuthenticatedUser.
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // Verifica si el parámetro está anotado con @CurrentUser y es del tipo AuthenticatedUser.
        return parameter.hasParameterAnnotation(CurrentUser.class)
            && Objects.equals(parameter.getParameterType(), AuthenticatedUser.class);
    }

    /**
     * Resuelve e inyecta el usuario autenticado en el parámetro del método controlador.
     *
     * @param parameter     Parámetro del método que requiere resolución.
     * @param mav           Contenedor del modelo y la vista (no utilizado aquí).
     * @param webRequest    Solicitud web actual.
     * @param binderFactory Fábrica de data binders (no utilizada aquí).
     * @return Objeto AuthenticatedUser extraído del atributo HTTP.
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mav,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        // Obtiene la solicitud HTTP nativa desde el contexto web.
        HttpServletRequest req = webRequest.getNativeRequest(HttpServletRequest.class);
        // Devuelve el usuario autenticado previamente almacenado en el atributo REQ_ATTR.
        return req.getAttribute(REQ_ATTR);
    }
}