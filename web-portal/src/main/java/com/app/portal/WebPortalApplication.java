package com.app.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicación Web (Spring Boot).
 * Escanea componentes dentro del paquete base com.app.portal.
 */
@SpringBootApplication
public class WebPortalApplication {
  public static void main(String[] args) {
    SpringApplication.run(WebPortalApplication.class, args);
  }
}
