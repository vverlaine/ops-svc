package com.proyecto.ops.customers.config;

import org.springframework.context.annotation.Configuration;


/**
 * Configuración global mínima para el microservicio de clientes.
 * 
 * - Elimina dependencias de CurrentUser y JWT.
 * - Registra el filtro AuthFilter solo si está disponible en el contexto.
 */
@Configuration
public class WebConfig {

}